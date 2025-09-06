package project.market.address;

import project.market.address.dto.AddressResponse;
import project.market.address.entity.Address;

import java.util.List;

public class AddressMapper {

    public static AddressResponse toAddressResponse (Address address){
        return AddressResponse.builder()
                .id(address.getId())
                .receiverName(address.getRecipientName())
                .receiverPhone(address.getRecipientPhone())
                .addressName(address.getAddressName())
                .postalCode(address.getPostalCode())
                .address(address.getAddress())
                .detailAddress(address.getDetailAddress())
                .request(address.getRequest())
                .isDefaultedAddress(address.getIsDefaultedAddress())
                .build();
    }

}
