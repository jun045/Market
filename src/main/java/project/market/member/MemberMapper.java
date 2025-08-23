package project.market.member;

import project.market.member.Entity.Member;
import project.market.member.dto.UpdateUserResponse;
import project.market.member.dto.UserDetailResponse;
import project.market.member.dto.UserSignupResponse;

public class MemberMapper {

    public static UserSignupResponse toUserSignupResponse(Member member){
        return UserSignupResponse.builder()
                .loginId(member.getLoginId())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .role(member.getRole())
                .level(member.getLevel())
                .memberStatus(member.getMemberStatus())
                .point(member.getPoint())
                .createdAt(member.getCreatedAt())
                .build();
    }

    public static UpdateUserResponse toUpdateUserResponse (Member member){
        return UpdateUserResponse.builder()
                .name(member.getName())
                .nickName(member.getNickname())
                .email(member.getEmail())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    public static UserDetailResponse toUserDetailResponse (Member member){
        return UserDetailResponse.builder()
                .loginId(member.getLoginId())
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .level(member.getLevel())
                .memberStatus(member.getMemberStatus())
                .point(member.getPoint())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
