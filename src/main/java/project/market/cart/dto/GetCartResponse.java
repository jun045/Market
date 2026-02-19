package project.market.cart.dto;

import lombok.Builder;
import project.market.PageResponse;

@Builder
public record GetCartResponse(CartResponse cartResponse,
                              PageResponse<CartItemResponse> cartItems) {
}
