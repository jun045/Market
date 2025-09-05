package project.market.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest (
        @Size(min = 2, max = 20) @Pattern(regexp = "^[a-zA-Z가-힣0-9_-]+$")  //대소문자, 한글, 숫자, _- 허용
        String name,
        @NotBlank @Email
        String email,
        @Size(min = 2, max = 20) @Pattern(regexp = "^[a-zA-Z가-힣0-9_-]+$")  //대소문자, 한글, 숫자, _- 허용
        String nickName){
}
