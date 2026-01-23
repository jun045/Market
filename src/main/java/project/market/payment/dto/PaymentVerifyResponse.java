package project.market.payment.dto;

import lombok.Builder;
import project.market.PurchaseOrder.entity.PayStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentVerifyResponse(boolean success,
                                    String message,
                                    String merchantUid,
                                    BigDecimal amount,
                                    PayStatus payStatus,
                                    LocalDateTime paidAt,
                                    String buyerName,
                                    String buyerEmail,
                                    String paidMethod,
                                    String pgProvider) {
}
