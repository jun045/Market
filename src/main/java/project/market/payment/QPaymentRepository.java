package project.market.payment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.market.PurchaseOrder.entity.PayStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QPaymentRepository {

    private final JPAQueryFactory queryFactory;
    private final QPayment qPayment = QPayment.payment;

    public Page<Payment> getAllPaymentsAndPaging (Long memberId, Pageable pageable){

        int size = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        List<Payment> payments = queryFactory
                .selectFrom(qPayment)
                .where(memberIdEq(memberId),
                        paymentTermCondition(),
                        paymentStatusCondition())
                .orderBy(qPayment.paidAt.desc())
                .offset((long) pageNumber * size)
                .limit(size)
                .fetch();

        Long count = queryFactory
                .select(qPayment.count())
                .from(qPayment)
                .where(memberIdEq(memberId),
                        paymentTermCondition(),
                        paymentStatusCondition())
                .fetchOne();

        long paymentsCount = count != null ? count : 0L;

        return new PageImpl<>(payments, pageable, paymentsCount);
    }

    private BooleanExpression memberIdEq (Long memberId){
        return memberId == null ? null : qPayment.member.id.eq(memberId);
    }

    //조회 기간 : 현재 날짜에서 최대 1년 전
    private BooleanExpression paymentTermCondition (){

        LocalDateTime oneYearAge = LocalDateTime.now().minusYears(1);

        return qPayment.paidAt.after(oneYearAge);
    }

    //결제 완료한 내역만 조회
    private BooleanExpression paymentStatusCondition (){
        return qPayment.payStatus.eq(PayStatus.PAID);
    }


}
