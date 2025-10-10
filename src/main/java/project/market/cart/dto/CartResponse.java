package project.market.cart.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CartResponse(Long cartId,
                           List<CartItemResponse> cartItemResponses,
                           long totalPrice,
                           int totalQuantity,
                           LocalDateTime updatedAt) {
}
