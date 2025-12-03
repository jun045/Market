package project.market.OrderItem;

import project.market.OrderItem.dto.OrderItemDetailResponse;

public class OrderItemMapper {

    public OrderItemMapper() {
    }

    public static OrderItemDetailResponse toDetailResponse(OrderItem item){
        return new OrderItemDetailResponse(
                item.getId(),
                item.getProductVariant().getId(),
                item.getProductVariant().getProduct().getProductName(),
                item.getProductVariant().getOptions(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }
}
