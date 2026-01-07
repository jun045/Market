package project.market.OrderItem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateOrderItemRequest(
        @JsonProperty("productVariantId") Long productVariantId,
        @JsonProperty("quantity") int quantity
) {}
