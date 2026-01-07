package project.market.payment;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.market.PageResponse;
import project.market.PurchaseOrder.PurchaseOrderRepository;
import project.market.PurchaseOrder.entity.PayStatus;
import project.market.PurchaseOrder.entity.PurchaseOrder;
import project.market.member.Entity.Member;
import project.market.member.event.EventType;
import project.market.member.event.MemberEventPublisher;
import project.market.payment.dto.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PurchaseOrderRepository orderRepository;
    private final IamportClient iamportClient;
    private final PaymentVerifier paymentVerifier;
    private final MemberEventPublisher eventPublisher;
    private final QPaymentRepository qPaymentRepository;

    //결제 전 검증
    @Transactional
    public PaymentIntentResponse preparePayment (Member member, Long purchaseOrderId){

        //테스트 결제 -> 결제액 100원 고정
        BigDecimal fixedAmount = BigDecimal.valueOf(100);

        PurchaseOrder purchaseOrder = orderRepository.findById(purchaseOrderId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 주문입니다.")
        );

        //구매 사용자, 주문 검증
        purchaseOrder.validateOwner(member);
        purchaseOrder.validatePurchasePayable();

        //포인트 사용검증 가능 여부 검증
        member.validatePoint(purchaseOrder.getUsedPoint());

        //payment 생성
        Payment paymentIntent = Payment.createPaymentIntent(member, purchaseOrder, fixedAmount);

        paymentRepository.save(paymentIntent);

        return PaymentMapper.toPaymentIntentResponse(paymentIntent);
    }

    //PG사 결제 내용 검증, 저장
    @Transactional
    public PaymentVerifyResponse verifyAndSave (Member member, PaymentVerifyRequest request){

        BigDecimal fixedAmount = BigDecimal.valueOf(100);

        try {
            //PG사 결제 정보 조회
            IamportResponse<com.siot.IamportRestClient.response.Payment> pgResponse = iamportClient.paymentByImpUid(request.impUid());
            var pg = pgResponse.getResponse();

            //pgResponse(결제 라이브러리에서 반환 해 줌) 검증
            paymentVerifier.pgResponseVerifier(pgResponse, request.merchantUid(), fixedAmount);

            PurchaseOrder purchaseOrder = orderRepository.findByMerchantUid(request.merchantUid()).orElseThrow(
                    () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
            );

            //주문 검증
            purchaseOrder.validateOwner(member);
            purchaseOrder.validatePurchasePayable();

            Payment payment = paymentRepository.findByMerchantUid(request.merchantUid()).orElseThrow(
                    () -> new IllegalArgumentException("결제 요청이 없습니다.")
            );

            //멱등성 보장 코드. PayStatus가 Ready일 때만 completedPayment 실행
            //payStatus.PAID 상태(결제 완료 된 상태)면 건너뛰고 결제 정보 DB 반영 계속
            if(payment.getPayStatus() == PayStatus.READY){
                payment.completePayment(); //PayStatus.READY -> PAID
            }

            //Pg 사에서 반환하는 결제 정보 DB에 저장
            payment.applyPgInfo(pg);

            //주문 쪽에 결제 정보 매핑
            purchaseOrder.setPaymentInfo(payment.getId(), pg.getImpUid());

            return PaymentMapper.success("결제 성공", payment.getMerchantUid(), pgResponse);
        }

        //결제 검증 실패 로직(결제 실패 메세지 반환)
        catch (IllegalArgumentException exception){
            return PaymentMapper.failure(exception.getMessage());
        }

        //pg사 오류로 결제 실패
        catch (IamportResponseException | IOException exception){
            log.error("PG사 결제 시스템 오류 발생", exception);
            return PaymentMapper.failure("결제 실패");
        }

    }

    //결제 후 처리
    @Transactional
    public PaymentConfirmResponse confirmPayment (Member member, PaymentConfirmRequest request){

        PurchaseOrder purchaseOrder = orderRepository.findByMerchantUid(request.merchantUid()).orElseThrow(
                () -> new IllegalArgumentException("해당 주문이 존재하지 않습니다.")
        );

        Payment payment = paymentRepository.findByImpUid(request.impUid()).orElseThrow(
                () -> new IllegalArgumentException("결제 내역이 존재하지 않습니다.")
        );

        //결제 검증
        purchaseOrder.validateOwner(member);
        payment.validateMatch(member);

        //재처리 시도 멱등 분기
        if(purchaseOrder.isPaymentCompleted()){
            PaymentMapper.toPaymentConfirmResponse(member, payment, List.of(
                    "이미 결제 후 처리 완료되었습니다.")
            );
        };

        //핵심 로직 - 주문 상태 PAID로 변경
        purchaseOrder.completePayment();

        List<String> messages = new ArrayList<>();

        //부가로직
        //1. 포인트 적립/사용 -> 실패 시 메세지 로그 저장, 메세지 반환
        try{
            member.usePoint(purchaseOrder.getUsedPoint());
            member.addPoints(purchaseOrder.getEarnPoint());
        } catch (Exception e){
            //실패 이벤트 발행(로그 저장)
            eventPublisher.memberPublishFailedEvent(
                    member.getId(),
                    purchaseOrder.getId(),
                    EventType.POINT_UPDATE_FAILED,
                    e.getMessage()
            );
            // 실패 메세지 반환
            messages.add("포인트 업데이트 실패: 관리자 문의");
        }

        //2. 등급 변경 -> 실패시 로그 저장, 메세지 반환
        try{
            member.addTotalSpentAmount(purchaseOrder.getPayAmount());
            member.updateMemberLevel();
        } catch (Exception e){
            //실패 이벤트 발행
            eventPublisher.memberPublishFailedEvent(
                    member.getId(),
                    purchaseOrder.getId(),
                    EventType.LEVEL_UPDATE_FAILED,
                    e.getMessage()
            );
            //메세지 반환
            messages.add("등급 업데이트 실패: 관리자 문의");
        }

        return PaymentMapper.toPaymentConfirmResponse(member, payment, messages);
    }

    //결제 내역 조회(1년 치)
    public PageResponse<PaymentResponse> getAllPayment (Member member, Pageable pageable){

        Page<Payment> paymentsAndPaging = qPaymentRepository.getAllPaymentsAndPaging(member.getId(), pageable);

        long totalElements = paymentsAndPaging.getTotalElements();
        List<Payment> payments = paymentsAndPaging.getContent();

        List<PaymentResponse> paymentResponseList = PaymentMapper.toPaymentResponseList(payments);

        return PageResponse.of(paymentResponseList, totalElements, pageable);

    }


}
