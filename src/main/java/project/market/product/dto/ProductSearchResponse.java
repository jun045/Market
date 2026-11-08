package project.market.product.dto;

import lombok.Builder;

@Builder
public record ProductSearchResponse(Long id,
                                    String categoryName,
                                    String brandName,
                                    String productName,
                                    int price,
                                    String thumb) {

    public static ProductSearchResponse from (ProductSearchRaw raw){
        return ProductSearchResponse.builder()
                .id(raw.productId())
                .categoryName(raw.categoryName())
                .brandName(raw.brandName())
                .productName(raw.productName())
                .price(raw.price())
                .thumb(raw.thumb())
                .build();
    }
}
