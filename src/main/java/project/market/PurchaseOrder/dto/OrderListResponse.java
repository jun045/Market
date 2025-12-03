package project.market.PurchaseOrder.dto;

import project.market.PurchaseOrder.OrderStatus;

import java.time.LocalDateTime;

//전체조회용 - id,주문상태,날짜,총가격,실제 결제 금액
public record OrderListResponse(
        Long id,
        OrderStatus orderStatus,
        LocalDateTime orderDate,
        int orderTotalPrice,
        int payAmount
) {
}
