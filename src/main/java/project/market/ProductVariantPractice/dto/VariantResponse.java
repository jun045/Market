package project.market.ProductVariantPractice.dto;

import java.util.Map;

public record VariantResponse(Long id,
                              Map<String, String> options,
                              int stock) {
}
