package project.market.PurchaseOrder.dto;

import project.market.OrderItem.dto.CreateOrderItemRequest;

import java.util.List;

public record CreateOrderRequest(int usedPoint,
                                 List<CreateOrderItemRequest> orderItems
) {
}
