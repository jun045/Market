package project.market.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.PageResponse;
import project.market.member.Entity.Member;
import project.market.payment.dto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("payments/{purchaseOrderId}/validation")
    public PaymentIntentResponse prePayment (@AuthenticationPrincipal (expression = "member") Member member,
                                             @PathVariable Long purchaseOrderId){
        return paymentService.preparePayment(member, purchaseOrderId);
    }

    @PostMapping("payments/verify")
    public PaymentVerifyResponse verifyPayment (@AuthenticationPrincipal(expression = "member") Member member,
                                                @RequestBody PaymentVerifyRequest request){

        return paymentService.verifyAndSave(member, request);
    }

    @PostMapping("payments/confirm")
    public PaymentConfirmResponse postPayment (@AuthenticationPrincipal (expression = "member") Member member,
                                               @RequestBody PaymentConfirmRequest request){

        return paymentService.confirmPayment(member, request);
    }

    @GetMapping("me/payments/all")
    public PageResponse<PaymentResponse> getAllPayments (@AuthenticationPrincipal (expression = "member")Member member,
                                                         @RequestParam (defaultValue = "1") int pageNumber,
                                                         @RequestParam (defaultValue = "20") int size){

        Pageable pageable = PageRequest.of(pageNumber, size);
        return paymentService.getAllPayment(member, pageable);
    }




}
