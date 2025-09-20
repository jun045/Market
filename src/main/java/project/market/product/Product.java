package project.market.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.Brand.Brand;

import java.util.ArrayList;
import java.util.List;

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
    private Integer listPrice; //정가

    private long viewCount = 0L; //상품 조회수 -> 인기상품 정렬 구현에 필요

//    @NotNull
//    private int salePrice; //최종가(세일가) -> DB 저장 없이 계산으로 반환


    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus; //판매상태

    private boolean isDeleted = false; //상품 삭제

    //카테고리:상품=1:n
    @ManyToOne
    private Category category;

    //좋아요 개수 -> service로직에 없어서 comment 처리
//    private int likeCount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionVariant> optionVariants = new ArrayList<>();

    @Builder
    public Product(String productName,
                   Brand brand,
                   String description,
                   String thumbnail,
                   String detailImage,
                   Integer listPrice,
//                   int salePrice,
                   ProductStatus productStatus,
                   boolean isDeleted,
                   Category category,
//                   int likeCount,
                   long viewCount,
                   List<OptionVariant> optionVariants) {
        this.productName = productName;
        this.brand = brand;
        this.description = description;
        this.thumbnail = thumbnail;
        this.detailImage = detailImage;
        this.listPrice = listPrice;
//        this.salePrice = salePrice;
        this.productStatus = productStatus;
        this.isDeleted = false;
        this.category = category;
//        this.likeCount = likeCount;
        this.viewCount = 0L;
        this.optionVariants = optionVariants;
    }



    //정보 수정
    public void update (
                   Category category,
                   Brand brand,
                   String productName,
                   String description,
                   String thumbnail,
                   String detailImage,
                   ProductStatus productStatus,
                   Integer listPrice
//                   List<OptionVariant> newOptionVariants
    ) {
        this.category = (category != null) ? category : this.category;  //수정 값이 들어오지 않으면 기존값 유지
        this.brand = (brand != null) ? brand : this.brand;
        this.productName = (productName != null) ? productName : this.productName;
        this.description = (description != null) ? description : this.description;
        this.thumbnail = (thumbnail != null) ? thumbnail : this.thumbnail;
        this.detailImage = (detailImage != null) ? detailImage : this.detailImage;
        this.productStatus = (productStatus != null) ? productStatus : this.productStatus;
        this.listPrice = (listPrice != null) ? listPrice : this.listPrice;
    }

    //소프트 딜리트 : 탈퇴시 true
    public void deletedProduct(){
        this.isDeleted = true;
    }

}