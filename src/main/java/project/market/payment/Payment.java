package project.market.payment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.PurchaseOrder.entity.PayStatus;
import project.market.PurchaseOrder.entity.PurchaseOrder;
import project.market.member.Entity.Member;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private PurchaseOrder purchaseOrder;

    @Column(unique = true)
    private String impUid;  //아임포트 결제 고유 id

    @NotNull
    @Column(nullable = false)
    private String merchantUid;  //주문 고유 id

    private String payMethod;  //카드

    private String pgProvider;  //KG이니시스

    @NotNull
    @Column(nullable = false)
    private BigDecimal amount;  //결제 금액

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayStatus payStatus = PayStatus.READY;

    private LocalDateTime paidAt;

    @Builder
    public Payment(Member member, PurchaseOrder purchaseOrder, String impUid, String merchantUid, String payMethod, String pgProvider, BigDecimal amount, PayStatus payStatus, LocalDateTime paidAt) {
        this.member = member;
        this.purchaseOrder = purchaseOrder;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.payMethod = payMethod;
        this.pgProvider = pgProvider;
        this.amount = amount;
        this.payStatus = PayStatus.READY;
        this.paidAt = paidAt;
    }

    //결제 의도 생성
    public static Payment createPaymentIntent (Member member, PurchaseOrder order, BigDecimal amount){
        return Payment.builder()
                .merchantUid(order.getMerchantUid())
                .amount(amount)
                .payStatus(PayStatus.READY)
                .member(member)
                .purchaseOrder(order)
                .build();
    }

    //Pg 사에서 반환하는 정보 백엔드로 받아서 DB에 저장
    public void applyPgInfo(com.siot.IamportRestClient.response.Payment pg) {
        this.impUid = pg.getImpUid();
        this.payMethod = pg.getPayMethod();
        this.pgProvider = pg.getPgProvider();
        this.amount = pg.getAmount();

        if (pg.getPaidAt() != null) {
            this.paidAt = LocalDateTime.ofInstant(
                    pg.getPaidAt().toInstant(),
                    ZoneId.of("Asia/Seoul")
            );
        }
    }

    //결제 완료 후 PayStatus Ready -> Paid
    public void completePayment (){
        if(this.payStatus != PayStatus.READY){
            throw new IllegalStateException("이미 결제 완료된 주문입니다.");
        }

        this.payStatus = PayStatus.PAID;

        if (this.paidAt == null) {
            this.paidAt = LocalDateTime.now();
        }
    }

    //결제 전 최종 검증
    public void validateMatch (Member paidMember){
        if(!this.getMerchantUid().equals(purchaseOrder.getMerchantUid())){
            throw new IllegalStateException("결제 정보와 주문 정보가 일치하지 않습니다.");
        }

        if(!this.member.getId().equals(paidMember.getId())){
            throw new IllegalStateException("본인의 결제만 처리할 수 있습니다.");
        }
    }


}
