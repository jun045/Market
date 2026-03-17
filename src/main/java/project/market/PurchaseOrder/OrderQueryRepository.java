package project.market.PurchaseOrder;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import project.market.PurchaseOrder.dto.OrderListResponse;
import project.market.PurchaseOrder.dto.OrderSearchDto;

import java.time.LocalDateTime;
import java.util.List;

import static project.market.PurchaseOrder.entity.QPurchaseOrder.purchaseOrder;
import static project.market.member.Entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<OrderListResponse> searchOrders(OrderSearchDto dto, Pageable pageable) {
        List<OrderListResponse> content = jpaQueryFactory
                .select(Projections.constructor(OrderListResponse.class,
                        purchaseOrder.id,
                        purchaseOrder.orderStatus,
                        purchaseOrder.orderTotalPrice,
                        purchaseOrder.payAmount
                ))
                .from(purchaseOrder)
                .leftJoin(purchaseOrder.member, member)
                .where(
                        merchantUidContains(dto.merchantUid()),
                        orderStatusEq(dto.orderStatus()),
                        dateBetween(dto.startDate(), dto.endDate()),
                        memberEmailEq(dto.memberEmail()),
                        purchaseOrder.isDeleted.isFalse()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(purchaseOrder.orderDate.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(purchaseOrder.count())
                .from(purchaseOrder)
                .leftJoin(purchaseOrder.member, member)
                .where(
                        merchantUidContains(dto.merchantUid()),
                        orderStatusEq(dto.orderStatus()),
                        dateBetween(dto.startDate(), dto.endDate()),
                        memberEmailEq(dto.memberEmail()),
                        purchaseOrder.isDeleted.isFalse()
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression merchantUidContains(String merchantUid) {
        return StringUtils.hasText(merchantUid) ? purchaseOrder.merchantUid.contains(merchantUid) : null;
    }

    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        return orderStatus != null ? purchaseOrder.orderStatus.eq(orderStatus) : null;
    }

    private BooleanExpression dateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) return null;
        if (startDate == null) return purchaseOrder.orderDate.loe(endDate);
        if (endDate == null) return purchaseOrder.orderDate.goe(startDate);
        return purchaseOrder.orderDate.between(startDate, endDate);
    }

    private BooleanExpression memberEmailEq(String memberEmail) {
        return StringUtils.hasText(memberEmail) ? member.email.eq(memberEmail) : null;
    }
}
