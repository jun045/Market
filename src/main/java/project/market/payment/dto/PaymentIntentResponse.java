package project.market.payment.dto;

import lombok.Builder;
import project.market.PurchaseOrder.entity.PayStatus;

import java.math.BigDecimal;

@Builder
public record PaymentIntentResponse(String merchantUid,
                                    BigDecimal amount,
                                    PayStatus payStatus,
                                    Long memberId,
                                    Long purchaseOrderId,
                                    String buyerName,
                                    String buyerEmail) {
}
