package project.market.payment.dto;

import lombok.Builder;
import project.market.PurchaseOrder.entity.PayStatus;
import project.market.payment.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponse(String impUid,
                              String merchantUid,
                              Long ordersId,
                              String pgProvider,
                              int finalOrdersAmount,  //주문 총액
                              BigDecimal amount,//결제된 금액 100원 고정
                              int usedPoint,
                              int earnPoint,
                              PayStatus payStatus,
                              LocalDateTime paidAt) {

    public static PaymentResponse from (Payment payment){

        return PaymentResponse.builder()
                .impUid(payment.getImpUid())
                .merchantUid(payment.getMerchantUid())
                .ordersId(payment.getPurchaseOrder().getId())
                .pgProvider(payment.getPgProvider())
                .finalOrdersAmount(payment.getPurchaseOrder().getPayAmount())
                .amount(payment.getAmount())
                .usedPoint(payment.getPurchaseOrder().getUsedPoint())
                .earnPoint(payment.getPurchaseOrder().getEarnPoint())
                .payStatus(payment.getPayStatus())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
