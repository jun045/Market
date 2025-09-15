package project.market.product.dto;

import project.market.product.ProductStatus;

import java.util.List;

public record UpdateProductRequest(Long parentCategoryId,
                                   Long categoryId,
                                   String name,
                                   Long brandId,
                                   String description,
                                   String thumb,
                                   String detailImage,
                                   ProductStatus productStatus,
                                   Integer listPrice,
                                   List<UpdateOptionVariantRequest> updateVariantRequest) {
}
