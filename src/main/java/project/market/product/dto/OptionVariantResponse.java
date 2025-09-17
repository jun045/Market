package project.market.product.dto;

import lombok.Builder;

@Builder
public record OptionVariantResponse(Long variantId,
                                    String optionSummary,
                                    Integer stock,
                                    Integer extraCharge,
                                    int salePrice) {
}
