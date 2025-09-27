package project.market.ProductVariant.dto;

public record AdminVariantResponse(Long id,
                                   String options,
                                   int stock,
                                   int extraCharge,
                                   long salePrice //db저장 안함
) {
}
