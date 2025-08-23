package project.market.member.dto;

import lombok.Builder;
import project.market.member.enums.Level;
import project.market.member.enums.MemberStatus;

import java.time.LocalDateTime;

@Builder
public record UserDetailResponse(String loginId,
                                 String name,
                                 String nickname,
                                 String email,
                                 Level level,
                                 MemberStatus memberStatus,
                                 int point,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt) {
}
