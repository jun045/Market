package project.market.address.dto;

import lombok.Builder;

@Builder
public record AddressResponse(Long id,
                              String receiverName,
                              String receiverPhone,
                              String addressName,
                              String postalCode,
                              String address,
                              String detailAddress,
                              String request,
                              Boolean isDefaultedAddress) {
}
