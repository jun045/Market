package project.market.ProductVariantPractice.dto;

import java.util.Map;

public record CreateVariantRequest(Map<String, String> options,
                                   int stock) {
}
