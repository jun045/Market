package project.market.payment.dto;

import lombok.Builder;
import project.market.PurchaseOrder.entity.PayStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PaymentConfirmResponse(String impUid,
                                     String merchantUid,
                                     String payMethod,
                                     String pgProvider,
                                     int finalPaymentAmount,
                                     BigDecimal amount,
                                     int usedPoint,
                                     int earnedPoint,
                                     int remainPoint,
                                     PayStatus payStatus,
                                     String buyerEmail,
                                     String buyerName,
                                     LocalDateTime paidAt,
                                     List<String> message) {
}
