package project.market.PurchaseOrder.dto;

import project.market.PurchaseOrder.OrderStatus;

import java.time.LocalDateTime;

public record OrderSearchDto(
        String merchantUid,
        OrderStatus orderStatus,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String memberEmail
) {}
