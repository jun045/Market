package project.market.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateReviewRequest(
        @Min(value = 1)
        @Max(value = 5)
        Integer rating,
        @Size(max = 300)
        String content) {
}
