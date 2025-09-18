package project.market.review.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewResponse(Long id,
                             String nickname,
                             String productName,
                             String optionSummary,
                             Integer rating,
                             String content,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
}
