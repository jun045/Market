package project.market.review;

import project.market.review.dto.ReviewResponse;

public class ReviewMapper {

    public ReviewResponse toReviewResponse (Review review){

        return ReviewResponse.from(review);
    }
}
