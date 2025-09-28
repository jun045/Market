package project.market.cart.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record CartItemResponse(Long id,
                               Long productId,
                               String productName,
                               String thumb,
                               Long variantId,
                               String options,
                               int quantity,
                               long salePrice,  //finalPrice
                               long itemPrice,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
}
