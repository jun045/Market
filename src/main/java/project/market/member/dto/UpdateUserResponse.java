package project.market.member.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateUserResponse(Long memberId,
                                 String name,
                                 String email,
                                 String nickName,
                                 LocalDateTime updatedAt) {
}
