package project.market.payment.dto;

import lombok.Builder;
import project.market.PurchaseOrder.entity.PayStatus;

import java.math.BigDecimal;

@Builder
public record PaymentVerifyResponse(boolean success,
                                    String message,
                                    String merchantUid,
                                    BigDecimal amount,
                                    PayStatus payStatus,
                                    String buyerName,
                                    String buyerEmail) {
}
