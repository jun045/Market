package project.market.review;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QReviewRepository {

    private final JPAQueryFactory queryFactory;
    private final QReview qReview = QReview.review;

    public Page<Review> getReviewsAndPaging (Long productId,
                                             Pageable pageable){

        int size = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        List<Review> reviews = queryFactory
                .selectFrom(qReview)
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
