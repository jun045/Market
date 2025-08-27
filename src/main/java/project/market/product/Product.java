package project.market.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.market.BaseEntity;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String productName;

    @NotNull
    private String description; //상품 설명

    @NotNull
    private String thumbnail; //상품 썸네일

    @NotNull
    private String detailImage; //상세이미지

    @NotNull
    private int listPrice; //정가

    @NotNull
    private int salePrice; //최종가(세일가)

    //판매상태
    @NotNull
    private productStatus productStatus = project.market.product.productStatus.SALE;

    private String brandName;

    private boolean isDeleted = false;

    //카테고리:상품=1:n
    @ManyToOne
    private Category category;

}
