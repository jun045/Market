package project.market.review.dto;

import lombok.Builder;
import project.market.review.Review;

import java.time.LocalDateTime;

@Builder
public record DeleteReviewResponse(Long reviewId,
                                   Boolean isDeleted,
                                   Boolean isReviewed,
                                   LocalDateTime deletedAt) {

    public static DeleteReviewResponse from (Review review){

        return DeleteReviewResponse.builder()
                .reviewId(review.getId())
                .isDeleted(review.getIsDeleted())
                .isReviewed(review.getOrderItem().getIsReviewed())
                .deletedAt(review.getDeletedAt())
                .build();
    }
}
