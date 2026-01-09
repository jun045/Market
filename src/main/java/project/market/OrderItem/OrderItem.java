package project.market.OrderItem;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.ProductVariant.ProductVariant;
import project.market.PurchaseOrder.OrderStatus;
import project.market.PurchaseOrder.entity.PurchaseOrder;
import project.market.member.Entity.Member;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int quantity; //구매 수량

    @NotNull
    private int unitPrice; //단가 (주문 시점의 상품 1개 가격) 스냅샷

    @NotNull
    private int totalPrice; //주문 시점의 상품*개수 총가격 스냅샷

    @ManyToOne(fetch = FetchType.LAZY)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductVariant productVariant;

    //리뷰 작성 확인용
    private Boolean isReviewed = false;

    @Builder
    public OrderItem(ProductVariant productVariant,
                     int quantity,
                     int unitPrice,
                     Boolean isReviewed
    ) {
        this.productVariant = Objects.requireNonNull(productVariant, "productVariant required");

        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        if (unitPrice < 0) throw new IllegalArgumentException("unitPrice는 0 이상이어야 합니다.");

        this.quantity = quantity;
        this.unitPrice = unitPrice;
        recalculateTotal(); //총액 계산
        this.isReviewed = false;
    }

    //orderitem 스스로 어떤 purchase에 속하는지 인지하는 역할
    //자기 상태 관리
    public void assignOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public void recalculateTotal() {
        long total = (long) this.unitPrice * (long) this.quantity;
        if (total > Integer.MAX_VALUE) {
            throw new IllegalStateException("총액이 int 범위를 초과합니다.");
        }
        this.totalPrice = (int) total;
    }

    //주문 생성 시 orderItem 객체 생성 + variant 기준으로 단가(unitPrice) 스냅샷 계산
    public static OrderItem createFromVariant(ProductVariant variant,
                                              int quantity) {
        if (variant == null) {
            throw new IllegalArgumentException("옵션 선택은 필수입니다 ");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        //재고 검사
        variant.validateStockOrThrow(quantity);

        //단가 계산 + 검증
        long finalPriceLong = variant.calculateFinalPrice();
        if (finalPriceLong < 0 || finalPriceLong > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("상품 단가 유효하지 않음");
        }
        int finalPrice = (int) finalPriceLong;

        //총액은 빌더에서 호출
        return OrderItem.builder()
                .productVariant(variant)
                .quantity(quantity)
                .unitPrice(finalPrice)
                .build();
    }

    //수량 증가 메서드 + totalprice 갱신
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        int newQuantity;
        try {
            newQuantity = Math.addExact(this.quantity, amount);
        } catch (ArithmeticException ex) {
            throw new IllegalStateException("수량 증가 시 정수 오버플로우가 발생했습니다.", ex);
        }

        //재고 유효성 검증
        productVariant.validateStockOrThrow(newQuantity);
        this.quantity += amount;
        recalculateTotal();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;

        OrderItem other = (OrderItem) o;

        //영속 상태 (DB에 저장된 후) id 기반 비교
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        //비영속 상태 (새로 생성된 상태) → 상품(variant) 기준 비교
        return productVariant != null
                && other.productVariant != null
                && productVariant.equals(other.productVariant);
    }

    @Override
    public int hashCode() {
        return (id != null)
                ? id.hashCode()
                : (productVariant != null ? productVariant.hashCode() : 0);
    }

    //리뷰 작성 가능 상태인지 검증 - 사용 위치 ReivewService
    public void validateReviewable (Member member){
        if(!purchaseOrder.getMember().getId().equals(member.getId())){
            throw new IllegalArgumentException("본인의 주문만 리뷰 작성 할 수 있습니다.");
        }

        if(!purchaseOrder.getOrderStatus().equals(OrderStatus.DELIVERED)){
            throw new IllegalArgumentException("리뷰는 배송이 완료된 후에 작성할 수 있습니다.");
        }

        if(isReviewed){
            throw new IllegalArgumentException("이미 리뷰를 작성하였습니다.");
        }
    }

    //리뷰 작성 완료 표시 - 사용 위치 ReviewService
    public void markReviewed (){
        if(isReviewed){
            throw new IllegalArgumentException("이미 리뷰를 작성하였습니다.");
        }
        this.isReviewed = true;
    }
}
