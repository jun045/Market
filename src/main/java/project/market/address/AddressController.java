package project.market.address;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.address.dto.AddressResponse;
import project.market.address.dto.CreateAddressRequest;
import project.market.address.dto.UpdateAddressRequest;
import project.market.member.Entity.Member;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AddressController {

    private final AddressService addressService;

    //주소 생성
    @PostMapping("api/v1/members/me/address")
    public AddressResponse createAddress(@AuthenticationPrincipal (expression = "member") Member member,
                                         @Valid @RequestBody CreateAddressRequest addressRequest){

        return addressService.create(member, addressRequest);
    }

    //주소 상세 조회
    @GetMapping("api/v1/members/me/address/{addressId}")
    public AddressResponse getDetailAddress(@AuthenticationPrincipal (expression = "member") Member member,
                                            @PathVariable (name = "addressId") Long addressId){

        return addressService.getDetail(member, addressId);
    }

    //주소 목록 조회
    @GetMapping("api/v1/members/me/address")
    public List<AddressResponse> getAddressList (@AuthenticationPrincipal (expression = "member") Member member){

        return addressService.getAddresses(member);
    }

    //배송 주소 수정
    @PatchMapping("api/v1/members/me/address/{addressId}")
    public AddressResponse updateAddress(@AuthenticationPrincipal (expression = "member") Member member,
                                         @PathVariable (name = "addressId") Long addressId,
                                         @Valid @RequestBody UpdateAddressRequest request){

        return addressService.update(member, addressId, request);
    }

    //기본값 주소 변경
    @PatchMapping("api/v1/members/me/address/{addressId}/default")
    public ResponseEntity<Void> setDefaultedAddress(@AuthenticationPrincipal (expression = "member") Member member,
                                                    @PathVariable Long addressId){

        addressService.setDefault(member, addressId);

        return ResponseEntity.ok().build();
    }


    //주소 삭제
    @DeleteMapping("api/v1/members/me/address/{addressId}")
    public ResponseEntity<Void> deleteAddress (@AuthenticationPrincipal (expression = "member") Member member,
                                               @PathVariable (name = "addressId") Long addressId){

        addressService.delete(member, addressId);

        return ResponseEntity.ok().build();
    }
}
