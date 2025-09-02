package project.market.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest (
        @Size(min = 2, max = 20) @Pattern(regexp = "^[a-zA-Z0-9_-]+$")
        String name,
        @NotBlank @Email
        String email,
        @Size(min = 2, max = 20) @Pattern(regexp = "^[a-zA-Z0-9_-]+$")
        String nickName){
}
