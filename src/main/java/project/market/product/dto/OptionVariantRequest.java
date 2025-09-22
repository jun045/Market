package project.market.product.dto;

import lombok.Builder;

@Builder
public record OptionVariantRequest (String optionSummary,
                                    Integer stock,
                                    Integer extraCharge){
}
