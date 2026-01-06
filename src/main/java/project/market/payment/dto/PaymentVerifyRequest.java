package project.market.payment.dto;

import java.math.BigDecimal;

public record PaymentVerifyRequest(String impUid,
                                   String merchantUid,
                                   BigDecimal amount) {
}
