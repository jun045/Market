package project.market.review.dto;

import java.time.LocalDateTime;

public record ReviewRaw(Long reviewId,
                        String nickname,
                        String productName,
                        String options,
                        Integer rating,
                        String content,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
}
