package project.market.member.dto;

import project.market.member.enums.Level;
import project.market.member.enums.MemberStatus;

import java.time.LocalDateTime;

public record UserListResponse(Long memberId,
                               String loginId,
                               String email,
                               Level level,
                               int point,
                               MemberStatus memberStatus,
                               LocalDateTime createdAt) {
}
