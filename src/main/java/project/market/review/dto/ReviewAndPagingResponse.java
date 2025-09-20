package project.market.review.dto;

import lombok.Builder;
import project.market.PageInfo;

import java.util.List;

@Builder
public record ReviewAndPagingResponse(List<ReviewResponse> reviewResponses,
                                      PageInfo pageInfo) {
}
