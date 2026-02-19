package project.market.cart.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CartResponse(Long cartId,
                           long totalPrice,
                           int totalQuantity,
                           LocalDateTime updatedAt) {
}
