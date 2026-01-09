package project.market.review;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

@Repository
public class QReviewRepository {

    private JPAQueryFactory queryFactory;
    private QReview qReview;


}
