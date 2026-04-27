package project.market.product.dto;

import project.market.product.ProductStatus;

public record AdminProductSearchRaw(Long productId,
                                    Long categoryId,
                                    String cateName,
                                    Long brandId,
                                    String brandName,
                                    String productName,
                                    int price,
                                    ProductStatus productStatus,
                                    boolean isDeleted) {
}
