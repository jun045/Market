package project.market.review;

import project.market.PageInfo;
import project.market.review.dto.ReviewAndPagingResponse;
import project.market.review.dto.ReviewResponse;

import java.util.List;

public class ReviewMapper {

    public static ReviewResponse toResponse (Review review){

        return ReviewResponse.builder()
                .id(review.getId())
                .nickname(review.getMember().getNickname())
                .productName(review.getProduct().getProductName())
                .optionSummary(review.getCartItem().getOptionVariant().getOptionSummary())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public static ReviewAndPagingResponse toReviewPagingResponse (List<ReviewResponse> reviewResponse, PageInfo pageInfo){

        return ReviewAndPagingResponse.builder()
                .reviewResponses(reviewResponse)
                .pageInfo(pageInfo)
                .build();
    }
}
