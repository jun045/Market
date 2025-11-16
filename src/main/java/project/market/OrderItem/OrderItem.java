package project.market.OrderItem;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.ProductVariant.ProductVariant;
import project.market.PurchaseOrder.PurchaseOrder;

@Setter
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
    private int unitPrice; //단가 (주문 시점의 가격 스냅샷)

    @ManyToOne(fetch = FetchType.LAZY)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductVariant productVariant;

    @Builder
    public OrderItem(ProductVariant productVariant,
                     int quantity,
                     int unitPrice
    ) {
        this.productVariant = productVariant;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    //orderitem 스스로 어떤 purchase에 속하는지 인지하는 역할
    //자기 상태 관리
    public void assignOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    //단일 상품 가격 계산 (단가*수량)
    //db저장 안되게 함, 캐시용
    @Transient
    public int calculateTotalPrice() {
        return this.unitPrice * this.quantity;
    }

    //주문 생성 시 상품 가격 스냅샷 생성
    public static OrderItem createFromVariant(ProductVariant variant,
                                              int quantity) {
        int finalPrice = (int) variant.calculateFinalPrice();
        return new OrderItem(variant, quantity, finalPrice);
    }

    //수량 증가 메서드
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        this.quantity += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;

        OrderItem other = (OrderItem) o;

        //영속 상태 (DB에 저장된 후) → id 기반 비교
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
}
