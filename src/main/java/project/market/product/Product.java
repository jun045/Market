package project.market.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.Brand.Brand;
import project.market.Cate.Category;

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
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus; //상품 판매 상태 (기본값: SALE)

    private boolean isDeleted = false; //상품 삭제

    //카테고리:상품=1:n
    @ManyToOne
    private Category category;

    //좋아요 개수
    private int likeCount=0;

    @Builder
    public Product(Brand brand,
                   String productName,
                   String description,
                   String thumbnail,
                   String detailImage,
                   int listPrice,
                   ProductStatus productStatus,
                   boolean isDeleted,
                   Category category,
                   int likeCount) {
        this.brand = brand;
        this.productName = productName;
        this.description = description;
        this.thumbnail = thumbnail;
        this.detailImage = detailImage;
        this.listPrice = listPrice;
        this.productStatus = productStatus != null ? productStatus : ProductStatus.SALE;
        this.isDeleted = isDeleted;
        this.category = category;
        this.likeCount = likeCount;  //좋아요 개수 (기본값 : 0)
    }

    //정보 수정
    public void update(String productName,
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
    public void deletedProduct() {
        this.isDeleted = true;
    }
}