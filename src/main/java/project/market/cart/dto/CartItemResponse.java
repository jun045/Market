package project.market.cart.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CartItemResponse(Long id,
                               Long productId,
                               String productName,
                               String thumb,
                               Long optionVariantId,
                               String optionSummary,
                               int quantity,
                               int salePrice,
                               int itemPrice,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
}
