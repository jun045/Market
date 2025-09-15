package project.market.product.dto;

import project.market.product.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(Long id,
                              String parentCategoryName,
                              String categoryName,
                              String brandName,
                              String name,
                              String description,
                              String thumb,
                              String detailImage,
                              ProductStatus productStatus,
                              Integer listPrice,
                              List<OptionVariantResponse> variantResponseList,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}
