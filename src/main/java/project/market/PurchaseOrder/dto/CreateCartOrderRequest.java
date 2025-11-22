package project.market.PurchaseOrder.dto;

import java.util.List;

public record CreateCartOrderRequest(List<Long> cartItemIds) {
}
