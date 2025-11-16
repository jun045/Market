package project.market.PurchaseOrder;

import com.mysema.commons.lang.Pair;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.OrderItem.OrderItem;
import project.market.ProductVariant.ProductVariant;
import project.market.member.Entity.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                         LocalDateTime orderDate,
                         LocalDateTime paidAt,
                         LocalDateTime shippedAt,
                         LocalDateTime deliveredAt,
                         LocalDateTime canceledAt,
                         int orderTotalPrice,
                         int usedPoint,
                         int earnPoint,
                         int payAmount,
                         Member member,
                         List<OrderItem> orderItems,
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
        this.orderItems = orderItems;
        this.isDeleted = isDeleted;
    }

    //주문 항목 추가(자신의 OrderItems 리스트에 추가)
    //상품추가+총액 재계산 역할
    //orderItem 생성
    //외부에서 오더아이템 생성 불가
    public OrderItem addOrderItem(ProductVariant variant,
                                  int quantity) {
        //같은 옵션 조합 있는지 확인
        for (OrderItem existing : orderItems) {
            if (existing.getProductVariant().equals(variant)) {
                existing.increaseQuantity(quantity);    //수량 증가

                return existing;
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
        for (Pair<ProductVariant, Integer> entry : items) {
            addOrderItem(entry.getFirst(), entry.getSecond());
        }
        recalculateOrderTotal(); // 마지막에 한 번만 호출
    }

    //계산 오류 방지 및 도메인 일관성
    //상품 삭제
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);   //리스트에서 상품 제거
        orderItem.assignOrder(null);    //연관관계 끊기
        recalculateOrderTotal();    //총액 다시 계산
    }

    //전체 주문 총액 재계산
    public void recalculateOrderTotal() {
        this.orderTotalPrice = orderItems.stream()
                .mapToInt(OrderItem::calculateTotalPrice)
                .sum();
        this.payAmount = Math.max(orderTotalPrice - usedPoint, 0);
    }

    //결제 완료 처리
    public void markAsPaid() {
        this.orderStatus = OrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.earnPoint = (int) (orderTotalPrice * 0.01); // 결제 완료 시 1% 적립
    }

    //주문 취소
    public void cancel() {
        if (this.orderStatus == OrderStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }
        this.orderStatus = OrderStatus.CANCELED;
        this.canceledAt = LocalDateTime.now(); //생성 시점에 자동 할당
    }

    //주문 수정 소프트 딜리트
    public void deletedOrder() {
        this.isDeleted = true;
    }

}
