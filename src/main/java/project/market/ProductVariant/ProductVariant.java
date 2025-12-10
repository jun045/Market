package project.market.ProductVariant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.product.Product;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @Column(nullable = false, length = 255)
    private String options;

    @NotNull
    @Column(nullable = false)
    private int stock;

    private int extraCharge = 0;  //추가금

    //Long타입으로 없으면 null값
    private Long discountPrice; //할인 가격

    private boolean isDeleted = false;

    //낙관적 락 : 재고 과부족 방지
    @Version
    private Long version;

    @Builder
    public ProductVariant(Product product,
                          String options,
                          int stock,
                          int extraCharge,
                          Long discountPrice,
                          boolean isDeleted) {
        this.product = product;
        this.options = options;
        this.stock = stock;
        this.extraCharge = extraCharge;
        this.discountPrice = discountPrice;
        this.isDeleted = isDeleted;
    }

    //소프트 딜리트
    public void deletedOption() {
        this.isDeleted = true;
    }

    //최종 판매 가격(DB저장 안함)
    //할인가 + 추가가격 or 정가 + 추가가격
    @Transient
    public long calculateFinalPrice() {
        long basePrice = (discountPrice != null)
                ? discountPrice
                : product.getListPrice();
        return basePrice + this.extraCharge;
    }

    //재고 확인
    public void validateStockOrThrow(int quantity) {
        if (isDeleted) {
            throw new IllegalStateException("삭제된 옵션입니다");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 함");
        }
        if (this.stock < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
    }

    //재고 차감
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stock -= quantity;
    }

    //판매 가능 여부 확인
    public boolean isSaleAvailable() {
        return !isDeleted               // 옵션 삭제되지 않고
                && product != null       // 상품 존재
                && !product.isDeleted() // 상품 삭제되지 않고
                && stock > 0;           // 재고 있음
    }
}
