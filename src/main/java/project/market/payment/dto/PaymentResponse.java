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

    public static PaymentResponse from (PaymentRaw raw){

        return PaymentResponse.builder()
                .impUid(raw.impUid())
                .merchantUid(raw.merchantUid())
                .ordersId(raw.purchaseOrderId())
                .pgProvider(raw.pgProvider())
                .finalOrdersAmount(raw.finalPayAmount())
                .amount(raw.amount())
                .usedPoint(raw.usedPoint())
                .earnPoint(raw.earnedPoint())
                .payStatus(raw.payStatus())
                .paidAt(raw.paidAt())
                .build();
    }
}
