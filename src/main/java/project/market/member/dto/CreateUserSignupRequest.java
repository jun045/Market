package project.market.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserSignupRequest(
        @NotBlank @Size(min = 4, max = 20) @Pattern(regexp = "^[a-zA-Z0-9_-]+$")  //대소문자, 숫자, _- 허용
        String loginId,
        @NotBlank @Size(min = 15, max = 30) @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).+$")  //대문자, 소문자, 숫자, 특수문자 조합 강제
        String password,
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_-]+$")
        String name,
        @Size(min = 2, max = 20) @Pattern(regexp = "^[a-zA-Z0-9_-]+$")  //대소문자, 숫자, _- 허용
        String nickname,
        @NotBlank @Email
        String email) {
}
