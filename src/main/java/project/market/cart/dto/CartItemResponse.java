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
                               Integer quantity,
                               Integer salePrice,
                               Integer itemPrice,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
}
