package project.market.address;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.market.address.dto.AddressResponse;
import project.market.address.dto.CreateAddressRequest;
import project.market.address.dto.UpdateAddressRequest;
import project.market.address.entity.Address;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;
    private final QAddressRepository qAddressRepository;

    //주소 생성
    @Transactional
    public AddressResponse create (Member member, CreateAddressRequest addressRequest){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new RuntimeException("로그인한 계정만 주소를 추가할 수 있습니다.")
        );

        long countAddress = addressRepository.countByMemberId(member.getId());

        //최초로 등록한 주소가 기본값 설정이 없으면 기본값 강제
        boolean makeDefault = countAddress == 0 || addressRequest.isDefaultedAddress();

        Address address = Address.builder()
                .member(user)
                .recipientName(addressRequest.receiverName())
                .recipientPhone(addressRequest.receiverPhone())
                .addressName(addressRequest.addressName())
                .postalCode(addressRequest.postalCode())
                .address(addressRequest.address())
                .detailAddress(addressRequest.detailAddress())
                .request(addressRequest.request())
                .isDefaultedAddress(makeDefault)
                .build();

        //기존 기본값 주소가 이미 있다면 기본값 해제
        if(addressRequest.isDefaultedAddress()){
            qAddressRepository.clearDefault(member.getId());
        }

        addressRepository.save(address);

        return AddressMapper.toAddressResponse(address);
    }

    //주소 상세 조회
    public AddressResponse getDetail(Member member, Long addressId) {

        Address address = addressRepository.findByIdAndMemberId(addressId, member.getId()).orElseThrow(
                () -> new RuntimeException("로그인 하지 않았거나 등록된 주소가 없습니다.")
        );

        return AddressMapper.toAddressResponse(address);
    }

    //주소 목록 조회
    public List<AddressResponse> getAddresses(Member member) {

        return addressRepository.findAllByMemberId(member.getId()).stream().map(
                        AddressMapper::toAddressResponse)
                .toList();
    }


    //주소 수정
    @Transactional
    public AddressResponse update(Member member, Long addressId, UpdateAddressRequest request) {

        Address address = addressRepository.findByIdAndMemberId(member.getId(), addressId).orElseThrow(
                () -> new RuntimeException("로그인 하지 않았거나 해당 주소가 존재하지 않습니다.")
        );

        address.updateAddress(request.receiverName(),
                request.receiverPhone(),
                request.addressName(),
                request.postalCode(),
                request.address(),
                request.detailAddress(),
                request.request());

        return AddressMapper.toAddressResponse(address);
    }

    //기본값 주소 수정
    @Transactional
    public void setDefault (Member member, Long addressId){

        Address address = addressRepository.findByIdAndMemberId(addressId, member.getId()).orElseThrow(
                () -> new RuntimeException("로그인하지 않았거나 해당 주소가 존재하지 않습니다.")
        );

        if (addressRepository.countByMemberId(member.getId()) <= 1 ){
            throw new RuntimeException("하나 이상의 주소가 등록되어 있어야 합니다.");
        }

        //이미 기본값이면 아무것도 안 함
        if (address.getIsDefaultedAddress()){
            return;
        }

        //기존의 기본값 해제
        qAddressRepository.clearDefault(member.getId());

        //새 기본값 설정
        address.setDefaultedAddress(true);

    }

    //주소 삭제
    @Transactional
    public void delete(Member member, Long addressId) {

        Address address = addressRepository.findByIdAndMemberId(addressId, member.getId()).orElseThrow(
                () -> new RuntimeException("로그인 하지 않았거나 해당 주소가 존재하지 않습니다.")
        );

        Boolean wasDefaulted = address.getIsDefaultedAddress();

        //주소가 하나만 등록되어 있으면 삭제 불가
        long addressCount = addressRepository.countByMemberId(member.getId());
        if(addressCount <= 1){
            throw new RuntimeException("하나 이상의 주소가 등록되어 있어야 합니다.");
        }

        //삭제
        addressRepository.delete(address);

        //삭제한 주소가 기본값이면 남은 주소들 중 최신 등록한 주소를 기본값으로 설정
        if(wasDefaulted){
            addressRepository.findFirstByMemberIdOrderByIdDesc(member.getId()).ifPresent(
                    address1 -> address1.setDefaultedAddress(true)
            );
        }
    }


}
