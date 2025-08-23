package project.market.member.dto;

public record CreateUserSignupRequest(String loginId,
                                      String password,
                                      String name,
                                      String nickname,
                                      String email) {
}
