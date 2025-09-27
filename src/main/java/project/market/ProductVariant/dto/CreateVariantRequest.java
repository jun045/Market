package project.market.ProductVariant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record CreateVariantRequest(

        @NotBlank(message = "옵션은 필수입니다.")
        String options,

        @Min(value = 0, message = "재고는 0 이상이어야 합니다")
        int stock,

        @Min(value = 0, message = "음수값 입력 불가합니다.")
        int extraCharge,

        //null 허용, 입력 없으면 정가
        Long discountPrice
) {
}
