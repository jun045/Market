package project.market.member.dto;

import lombok.Builder;
import project.market.member.enums.Level;
import project.market.member.enums.MemberStatus;
import project.market.member.enums.Role;

import java.time.LocalDateTime;

@Builder
public record UserSignupResponse(String loginId,
                                 String name,
                                 String nickname,
                                 String email,
                                 Role role,
                                 Level level,
                                 MemberStatus memberStatus,
                                 int point,
                                 LocalDateTime createdAt) {

}
