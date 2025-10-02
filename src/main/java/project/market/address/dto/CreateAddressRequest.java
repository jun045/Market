package project.market.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(
        Long memberId,
        @NotBlank @Size(min = 1, max = 15)
        String receiverName,
        @NotBlank @Size(min = 10, max = 13) @Pattern(regexp = "^[0-9\\-]{10,13}$")
        String receiverPhone,
        @NotBlank @Size(min = 1, max = 20)
        String addressName,
        @NotBlank @Size(min = 5, max = 5) @Pattern(regexp = "^[0-9]{5}$")
        String postalCode,
        @NotBlank @Size(min = 2, max = 30)
        String address,
        @NotBlank @Size(min = 2, max = 30)
        String detailAddress,
        String request,
        Boolean isDefaultedAddress) {
}
