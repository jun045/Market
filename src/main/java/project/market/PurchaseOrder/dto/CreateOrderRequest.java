package project.market.PurchaseOrder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import project.market.OrderItem.dto.CreateOrderItemRequest;

import java.util.List;

public record CreateOrderRequest(
        @JsonProperty("usedPoint") int usedPoint,
        @JsonProperty("orderItems") List<CreateOrderItemRequest> orderItems
) {}
