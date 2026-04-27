package project.market.review;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.market.OrderItem.QOrderItem;
import project.market.PurchaseOrder.entity.QPurchaseOrder;
import project.market.member.Entity.QMember;
import project.market.review.dto.ReviewRaw;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QReviewRepository {

    private final JPAQueryFactory queryFactory;
    private final QReview qReview = QReview.review;
    private final QMember qMember = QMember.member;
    private final QOrderItem qOrderItem = QOrderItem.orderItem;

    public Page<ReviewRaw> getReviewsAndPaging (Long productId,
                                             Pageable pageable){

        int size = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

//        List<Review> reviews = queryFactory
//                .selectFrom(qReview)
//                .where(productIdEq(productId),
//                        qReview.isDeleted.isFalse())
//                .orderBy(qReview.createdAt.desc())
//                .offset((long) pageNumber * size)
//                .limit(size)
//                .fetch();
        List<ReviewRaw> reviews = queryFactory
                .select(Projections.constructor(ReviewRaw.class,
                        qReview.id,
                        qMember.nickname,
                        qReview.product.productName,
                        qOrderItem.productVariant.options,
                        qReview.rating,
                        qReview.content,
                        qReview.createdAt,
                        qReview.updatedAt
                ))
                .from(qReview)
                .join(qReview.member, qMember)
                .join(qReview.orderItem, qOrderItem)
                .where(productIdEq(productId),
                        qReview.isDeleted.isFalse())
                .orderBy(qReview.createdAt.desc())
                .offset((long) pageNumber * size)
                .limit(size)
                .fetch();

        Long count = queryFactory
                .select(qReview.count())
                .from(qReview)
                .where(productIdEq(productId),
                        qReview.isDeleted.isFalse())
                .fetchOne();

        long reviewCount = count != null ? count : 0L;

        return new PageImpl<>(reviews, pageable, reviewCount);

    }

    private BooleanExpression productIdEq (Long productId){
        return productId == null ? null : qReview.product.id.eq(productId);
    }

}
