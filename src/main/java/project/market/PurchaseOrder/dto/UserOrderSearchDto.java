package project.market.PurchaseOrder.dto;

import java.time.LocalDateTime;

public record UserOrderSearchDto(
        String productName,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
