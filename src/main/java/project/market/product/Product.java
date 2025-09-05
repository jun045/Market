package project.market.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.Brand.Brand;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //브랜드도 따로 분리해야 한 브랜드 상품 다 보기 가능
    //private String brandName;
    @ManyToOne
    private Brand brand;

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


    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus; //판매상태

    private boolean isDeleted = false; //상품 삭제

    //카테고리:상품=1:n
    @ManyToOne
    private Category category;

    //좋아요 개수
    private int likeCount;

    @Builder
    public Product(String productName,
                   String description,
                   String thumbnail,
                   String detailImage,
                   int listPrice,
                   int salePrice,
                   ProductStatus productStatus,
                   boolean isDeleted,
                   Category category,
                   int likeCount) {
        this.productName = productName;
        this.description = description;
        this.thumbnail = thumbnail;
        this.detailImage = detailImage;
        this.listPrice = listPrice;
        this.salePrice = salePrice;
        this.productStatus = productStatus;
        this.isDeleted = isDeleted;
        this.category = category;
        this.likeCount = likeCount;
    }

    //할인된 가격 계산

    //정보 수정
    public void update (String productName,
                   String description,
                   String thumbnail,
                   String detailImage,
                   int listPrice) {
        this.productName = productName;
        this.description = description;
        this.thumbnail = thumbnail;
        this.detailImage = detailImage;
        this.listPrice = listPrice;
    }

    //소프트 딜리트 : 탈퇴시 true
    public void deletedProduct(){
        this.isDeleted = true;
    }
}