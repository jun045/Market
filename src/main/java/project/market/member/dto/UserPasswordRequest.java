package project.market.member.dto;

public record UserPasswordRequest(String existingPassword,
                                  String newPassword,
                                  String confirmNewPassword) {
}
