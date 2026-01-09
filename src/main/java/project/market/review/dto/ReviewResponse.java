package project.market.review.dto;

import lombok.Builder;
import project.market.review.Review;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReviewResponse(Long reviewId,
                             String nickname,
                             String productName,
                             String option,
                             Integer rating,
                             String content,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {

    public static ReviewResponse from(Review review){
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .nickname(review.getMember().getNickname())
                .productName(review.getProduct().getProductName())
                .option(review.getOrderItem().getProductVariant().getOptions())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public static List<ReviewResponse> fromList (List<Review> reviews){
        return reviews.stream().map(ReviewResponse::from).toList();
    }
}
