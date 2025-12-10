package project.market.PurchaseOrder.entity;

import com.mysema.commons.lang.Pair;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.OrderItem.OrderItem;
import project.market.ProductVariant.ProductVariant;
import project.market.PurchaseOrder.OrderStatus;
import project.market.member.Entity.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PurchaseOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문 상태

    @NotNull
    private LocalDateTime orderDate; //주문 일시

    private LocalDateTime paidAt;       // 결제 완료 시간
    private LocalDateTime shippedAt;    // 배송 시작 시간 (상태표시용)
    private LocalDateTime deliveredAt;  // 배송 완료 시간 (상태표시용)
    private LocalDateTime canceledAt;  // 주문 취소 시간

    @NotNull
    private int orderTotalPrice = 0; //주문 총액

    @NotNull
    private int usedPoint = 0; //사용 포인트

    @NotNull
    private int earnPoint = 0; // 적립 포인트

    @NotNull
    private int payAmount = 0; //결제 예정 금액 = 주문 총액 - 사용 포인트 (pg연동에서 사용)

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "purchaseOrder",
            cascade = CascadeType.ALL, //purchaseOrder에서 저장,삭제 시 orderitem 자동 저장,삭제
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Builder
    public PurchaseOrder(OrderStatus orderStatus,
                         LocalDateTime paidAt,
                         LocalDateTime shippedAt,
                         LocalDateTime deliveredAt,
                         LocalDateTime canceledAt,
                         int orderTotalPrice,
                         int usedPoint,
                         int earnPoint,
                         int payAmount,
                         Member member,
                         boolean isDeleted) {
        this.orderStatus = orderStatus;
        this.orderDate = LocalDateTime.now();
        this.paidAt = paidAt;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
        this.canceledAt = canceledAt;
        this.orderTotalPrice = orderTotalPrice;
        this.usedPoint = usedPoint;
        this.earnPoint = earnPoint;
        this.payAmount = payAmount;
        this.member = member;
        this.isDeleted = isDeleted;
    }

    //주문 항목 추가(자신의 OrderItems 리스트에 추가)
    //상품추가+총액 재계산 역할
    //orderItem 생성
    //외부에서 오더아이템 생성 불가
    public OrderItem addOrderItem(ProductVariant variant,
                                  int quantity) {
        if (variant == null) throw new IllegalArgumentException("상품 옵션 필수 선택");
        if (quantity <= 0) throw new IllegalArgumentException("수량은 0 이상이어야 함");

        //db에 같은 옵션 조합 있는지 id 확인
        if (variant.getId() != null) {
            for (OrderItem existing : orderItems) {
                ProductVariant existVariant = existing.getProductVariant();
                if (existVariant != null && existVariant.getId() != null
                        && existVariant.getId().equals(variant.getId())) {
                    //있으면 수량만 증가(재고 차감은 서비스 클래스에서)
                    existing.increaseQuantity(quantity);
                    return existing;
                }
            }
        }
        //db저장 전 상태일때
        for (OrderItem existing : orderItems) {
            ProductVariant ev = existing.getProductVariant();
            if (ev != null && ev.getId() == null && variant.getId() == null) {
                if (Objects.equals(ev.getProduct().getId(), variant.getProduct().getId())
                        && Objects.equals(ev.getOptions(), variant.getOptions())) {
                    existing.increaseQuantity(quantity);

                    return existing;
                }
            }
        }

        //없으면 생성
        OrderItem item = OrderItem.createFromVariant(variant, quantity);
        item.assignOrder(this);      //assignOrder 호출
        orderItems.add(item);

        return item;
    }

    //여러 아이템 한번에 추가
    public void addOrderItemsBatch(List<Pair<ProductVariant, Integer>> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("주문할 상품이 없습니다");
        }
        for (Pair<ProductVariant, Integer> entry : items) {
            addOrderItem(entry.getFirst(), entry.getSecond());
        }
        recalculateOrderTotal(); // 마지막에 한 번만 호출
    }

    //계산 오류 방지 및 도메인 일관성
    //상품 삭제
    public void removeOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            return;
        }
        orderItems.remove(orderItem);   //리스트에서 상품 제거
        orderItem.assignOrder(null);    //연관관계 끊기
        recalculateOrderTotal();    //총액 다시 계산
    }

    //전체 주문 총액 재계산
    public void recalculateOrderTotal() {
        this.orderTotalPrice = orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
        this.payAmount = Math.max(orderTotalPrice - usedPoint, 0);
    }

    //결제 완료 처리
    public void markAsPaid() {
        this.orderStatus = OrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.earnPoint = (int) (orderTotalPrice * 0.01); //결제 완료 시 1% 적립
    }

    //주문 취소
    public void cancel() {
        if (this.orderStatus == OrderStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }
        this.orderStatus = OrderStatus.CANCELED;
        this.canceledAt = LocalDateTime.now(); //생성 시점에 자동 할당
    }

    //소프트 딜리트
    public void deletedOrder() {
        this.isDeleted = true;
    }
}
