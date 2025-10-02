package project.market.address.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAddressRequest(
        Long memberId,
        @Size(min = 1, max = 15)
        String receiverName,
        @Size(min = 10, max = 13) @Pattern(regexp = "^[0-9_-]+$")
        String receiverPhone,
        @Size(min = 1, max = 20)
        String addressName,
        @Size(min = 5, max = 5) @Pattern(regexp = "^[0-9]+$")
        String postalCode,
        @Size(min = 2, max = 30)
        String address,
        @Size(min = 2, max = 30)
        String detailAddress,
        String request) {
}
