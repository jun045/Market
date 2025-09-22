package project.market.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotNull
        @Min(value = 1)
        @Max(value = 5)
        Integer rating,
        @NotNull
        @Size(max = 300)
        String content) {
}
