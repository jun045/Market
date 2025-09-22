package project.market.ProductVariantPractice;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.product.Product;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "option_variant_options",
            joinColumns = @JoinColumn(name = "option_variant_id"))
    @MapKeyColumn(name = "option_name")
    @Column(name = "option_value")
    private Map<String, String> options = new HashMap<>(); //키-밸류 {"색상":"화이트", "사이즈":"L"}

    @NotNull
    private int stock;

    private int discountRate; //할인율

    private boolean isDeleted = false;

    @Builder
    public ProductVariant(Product product,
                          Map<String, String> options,
                          int stock,
                          int discountRate) {
        this.product = product;
        this.options = options;
        this.stock = stock;
        this.discountRate = discountRate;
    }

    //소프트 딜리트
    public void deletedOption(){
        this.isDeleted = true;
    }
}
