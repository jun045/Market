package project.market.review;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QReview qReview = QReview.review;

    public List<Review> getReviewsAndPaging (Long productId,
                                             int pageNumber,
                                             int size){
        return queryFactory
                .selectFrom(qReview)
                .where(productIdEq(productId),
                        qReview.isDeleted.isFalse())
                .orderBy(qReview.createdAt.desc())
                .offset((long) pageNumber * size)
                .limit(size)
                .fetch();
    }

    public long getTotalReviews (Long productId){
        Long totalReviews = queryFactory
                .select(qReview.count())
                .from(qReview)
                .where(productIdEq(productId),
                        qReview.isDeleted.isFalse())
                .fetchOne();

        return totalReviews != null ? totalReviews : 0L;
    }

    private BooleanExpression productIdEq (Long productId){
        return productId == null ? null : qReview.product.id.eq(productId);
    }


}
