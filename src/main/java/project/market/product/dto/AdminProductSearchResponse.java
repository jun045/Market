package project.market.product.dto;

import lombok.Builder;
import project.market.product.ProductStatus;

@Builder
public record AdminProductSearchResponse(Long productId,
                                         Long categoryId,
                                         String cateName,
                                         Long brandId,
                                         String brandName,
                                         String productName,
                                         int price,
                                         ProductStatus productStatus,
                                         boolean isDeleted
                                         ) {

    public static AdminProductSearchResponse from (AdminProductSearchRaw raw){
        return AdminProductSearchResponse.builder()
                .productId(raw.productId())
                .categoryId(raw.categoryId())
                .cateName(raw.cateName())
                .brandId(raw.brandId())
                .brandName(raw.brandName())
                .productName(raw.productName())
                .price(raw.price())
                .productStatus(raw.productStatus())
                .isDeleted(raw.isDeleted())
                .build();
    }
}
