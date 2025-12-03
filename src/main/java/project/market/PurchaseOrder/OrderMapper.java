package project.market.PurchaseOrder;

import org.springframework.stereotype.Component;
import project.market.OrderItem.dto.OrderItemDetailResponse;
import project.market.PurchaseOrder.dto.OrderDetailResponse;
import project.market.PurchaseOrder.dto.OrderListResponse;
import project.market.PurchaseOrder.entity.PurchaseOrder;

import java.util.List;

public class OrderMapper {

    public OrderMapper() {
    }

    public static OrderDetailResponse toDetailResponse(PurchaseOrder order,
                                                       List<OrderItemDetailResponse> items) {
        return new OrderDetailResponse(
                order.getId(),
                order.getOrderStatus(),
                order.getOrderDate(),
                order.getOrderTotalPrice(),
                order.getUsedPoint(),
                order.getEarnPoint(),
                order.getPayAmount(),
                items
        );
    }

    public static OrderListResponse toListResponse(PurchaseOrder order) {
        return new OrderListResponse(
                order.getId(),
                order.getOrderStatus(),
                order.getOrderDate(),
                order.getOrderTotalPrice(),
                order.getPayAmount()
        );
    }
}
