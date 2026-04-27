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
import project.market.PurchaseOrder.dto.OrderDetailResponse;
import project.market.PurchaseOrder.dto.OrderListResponse;
import project.market.PurchaseOrder.dto.OrderSearchDto;
import project.market.PurchaseOrder.dto.UserOrderSearchDto;
import project.market.PurchaseOrder.entity.PurchaseOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static project.market.PurchaseOrder.entity.QPurchaseOrder.purchaseOrder;
import static project.market.member.Entity.QMember.member;
import static project.market.OrderItem.QOrderItem.orderItem;
import static project.market.ProductVariant.QProductVariant.productVariant;
import static project.market.product.QProduct.product;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    //관리자용 -전체조회 + 검색
    public Page<OrderListResponse> searchOrders(OrderSearchDto dto, Pageable pageable) {
        List<OrderListResponse> content = jpaQueryFactory
                .select(Projections.constructor(OrderListResponse.class,
                        purchaseOrder.id,
                        purchaseOrder.orderStatus,
                        purchaseOrder.orderDate,
                        purchaseOrder.orderTotalPrice,
                        purchaseOrder.payAmount
                ))
                .from(purchaseOrder)
                .leftJoin(purchaseOrder.member, member)
                .where(
                        merchantUidStartWith(dto.merchantUid()),
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
                        merchantUidStartWith(dto.merchantUid()),
                        orderStatusEq(dto.orderStatus()),
                        dateBetween(dto.startDate(), dto.endDate()),
                        memberEmailEq(dto.memberEmail()),
                        purchaseOrder.isDeleted.isFalse()
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    //사용자용 - 전체조회 + 검색
    public Page<OrderListResponse> searchUserOrders(Long memberId, UserOrderSearchDto dto, Pageable pageable) {
        List<OrderListResponse> content = jpaQueryFactory
                .select(Projections.constructor(OrderListResponse.class,
                        purchaseOrder.id,
                        purchaseOrder.orderStatus,
                        purchaseOrder.orderDate,
                        purchaseOrder.orderTotalPrice,
                        purchaseOrder.payAmount
                ))
                .from(purchaseOrder)
                .where(
                        purchaseOrder.member.id.eq(memberId),
                        merchantUidStartWith(dto.merchantUid()),
                        dateBetween(dto.startDate(), dto.endDate()),
                        purchaseOrder.isDeleted.isFalse()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(purchaseOrder.orderDate.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(purchaseOrder.count())
                .from(purchaseOrder)
                .where(
                        purchaseOrder.member.id.eq(memberId),
                        merchantUidStartWith(dto.merchantUid()),
                        dateBetween(dto.startDate(), dto.endDate()),
                        purchaseOrder.isDeleted.isFalse()
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    //상세조회 (관리자, 사용자 공통)
    public Optional<PurchaseOrder> findOrderDetail(Long orderId){
        PurchaseOrder result = jpaQueryFactory
                .selectFrom(purchaseOrder)
                .leftJoin(purchaseOrder.member, member).fetchJoin()
                .leftJoin(purchaseOrder.orderItems,orderItem).fetchJoin()
                .leftJoin(orderItem.productVariant,productVariant).fetchJoin()
                .leftJoin(productVariant.product, product).fetchJoin()
                .where(
                        purchaseOrder.id.eq(orderId),
                        purchaseOrder.isDeleted.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression merchantUidStartWith(String merchantUid) {
        return StringUtils.hasText(merchantUid) ? purchaseOrder.merchantUid.startsWith(merchantUid) : null;
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
