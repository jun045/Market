package project.market.OrderItem.dto;

public record CreateOrderItemRequest(Long productVariantId,
                                     int quantity) {
}
