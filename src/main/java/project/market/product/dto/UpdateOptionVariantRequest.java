package project.market.product.dto;

public record UpdateOptionVariantRequest(Long variantId,
                                         String optionSummary,
                                         Integer stock,
                                         Integer extraCharge) {
}
