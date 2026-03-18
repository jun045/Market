package project.market.payment.dto;

import project.market.PurchaseOrder.entity.PayStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRaw(String impUid,
                         String merchantUid,
                         Long purchaseOrderId,
                         String pgProvider,
                         int finalPayAmount,
                         BigDecimal amount,
                         int usedPoint,
                         int earnedPoint,
                         PayStatus payStatus,
                         LocalDateTime paidAt) {
}
