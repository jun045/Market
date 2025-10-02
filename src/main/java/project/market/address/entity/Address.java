package project.market.address.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.member.Entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String addressName;

    @NotNull
    private String recipientName;

    @NotNull
    private String recipientPhone;

    @NotNull
    private String postalCode;

    @NotNull
    private String address;

    @NotNull
    private String detailAddress;

    private String request;

    private Boolean isDefaultedAddress;

    @ManyToOne
    private Member member;

    @Builder
    public Address(Long id, String addressName, String recipientName, String recipientPhone, String postalCode, String address, String detailAddress, String request, Boolean isDefaultedAddress, Member member) {
        this.id = id;
        this.addressName = addressName;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.postalCode = postalCode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.request = request;
        this.isDefaultedAddress = isDefaultedAddress;
        this.member = member;
    }

    public void setDefaultedAddress(Boolean isDefaultedAddress) {
        this.isDefaultedAddress = true;
    }

    public void updateAddress(String recipientName, String recipientPhone, String addressName, String postalCode, String address, String detailAddress, String request) {
        this.recipientName = (recipientName != null) ? recipientName:this.recipientName;
        this.recipientPhone = (recipientPhone != null) ? recipientPhone:this.recipientPhone;
        this.addressName = (addressName != null) ? addressName:this.addressName;
        this.postalCode = (postalCode != null) ? postalCode:this.postalCode;
        this.address = (address != null) ? address:this.address;
        this.detailAddress = (detailAddress != null) ? detailAddress:this.detailAddress;
        this.request = (request != null) ? request:this.request;
    }

}
