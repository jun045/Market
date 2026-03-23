package project.market.PurchaseOrder.dto;

import java.time.LocalDateTime;

public record UserOrderSearchDto(
        String merchantUid,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
