package project.market.product.dto;

import lombok.Builder;
import project.market.Brand.dto.CreateBrandRequest;
import project.market.product.OptionVariantRepository;
import project.market.product.ProductStatus;

import java.util.List;

@Builder
public record CreateProductRequest(Long parentCategoryId,
                                   Long categoryId,
                                   String name,
                                   Long brandId,
                                   String description,
                                   String thumb,
                                   String detailImage,
                                   ProductStatus productStatus,
                                   Integer listPrice,
                                   List<OptionVariantRequest> variantRequest) {
}
