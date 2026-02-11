package project.market.cart.dto;

import java.time.LocalDateTime;

public record CartItemRaw(Long id,
                          Long productId,
                          String productName,
                          String thumb,
                          Long variantId,
                          String options,
                          int quantity,
                          int listPrice,
                          int extraCharge,
                          Long discountPrice,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt) {
}
