package project.market.payment;

import com.siot.IamportRestClient.response.IamportResponse;
import project.market.member.Entity.Member;
import project.market.payment.dto.PaymentConfirmResponse;
import project.market.payment.dto.PaymentIntentResponse;
import project.market.payment.dto.PaymentResponse;
import project.market.payment.dto.PaymentVerifyResponse;

import java.util.List;

public class PaymentMapper {

    public static PaymentIntentResponse toPaymentIntentResponse (Payment payment){
        return PaymentIntentResponse.builder()
                .merchantUid(payment.getMerchantUid())
                .amount(payment.getAmount())
                .payStatus(payment.getPayStatus())
                .memberId(payment.getMember().getId())
                .purchaseOrderId(payment.getPurchaseOrder().getId())
                .build();
    }

    public static PaymentVerifyResponse success (String message, Payment payment, IamportResponse<com.siot.IamportRestClient.response.Payment> pgResponse){
        return PaymentVerifyResponse.builder()
                .success(true)
                .message(message)
                .merchantUid(payment.getMerchantUid())
                .amount(pgResponse.getResponse().getAmount())
                .buyerName(pgResponse.getResponse().getBuyerName())
                .buyerEmail(pgResponse.getResponse().getBuyerEmail())
                .payStatus(payment.getPayStatus())
                .paidAt(payment.getPaidAt())
                .paidMethod(pgResponse.getResponse().getPayMethod())
                .pgProvider(payment.getPgProvider())
                .build();
    }

    public static PaymentVerifyResponse failure (String message){
        return PaymentVerifyResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public static PaymentConfirmResponse toPaymentConfirmResponse (Member member, Payment payment, List<String> messages){
        return PaymentConfirmResponse.builder()
                .impUid(payment.getImpUid())
                .merchantUid(payment.getMerchantUid())
                .payMethod(payment.getPayMethod())
                .pgProvider(payment.getPgProvider())
                .amount(payment.getAmount())
                .finalPaymentAmount(payment.getPurchaseOrder().getPayAmount())
                .usedPoint(payment.getPurchaseOrder().getUsedPoint())
                .earnedPoint(payment.getPurchaseOrder().getEarnPoint())
                .remainPoint(member.getPoint())
                .payStatus(payment.getPayStatus())
                .paidAt(payment.getPaidAt())
                .message(messages)
                .build();
    }

    public static List<PaymentResponse> toPaymentResponseList (List<Payment> payments){

        return payments.stream().map(PaymentResponse::from)
                .toList();

    }
}
