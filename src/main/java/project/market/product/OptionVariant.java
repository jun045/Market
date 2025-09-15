package project.market.product;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.market.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OptionVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String optionSummary;

    @NotNull
    private Integer stock;

//    @NotNull
//    private Integer salePrice;  //listPrice + extraCharge

    private Integer extraCharge;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Builder
    public OptionVariant(Long id, String optionSummary, Integer stock, Integer extraCharge, Integer salePrice, Product product) {
        this.id = id;
        this.optionSummary = optionSummary;
        this.stock = stock;
        this.extraCharge = extraCharge;
//        this.salePrice = salePrice;
        this.product = product;
    }

    //OptionVariant -> Product 양방향 편의 메서드의 역방향 메서드(setter 경고 무시)
    public void setProduct(Product product){
        this.product = product;
    }

    //salePrice 계산
    @Transient
    public Integer getSalePrice(){
        return this.product.getListPrice() + this.extraCharge;
    }

    public void updateVariant(String optionSummary, Integer stock, Integer extraCharge) {
        this.optionSummary = (optionSummary != null) ? optionSummary : this.optionSummary;
        this.stock = (stock != null) ? stock : this.stock;
        this.extraCharge = (extraCharge != null) ? extraCharge : this.extraCharge;
    }
}
