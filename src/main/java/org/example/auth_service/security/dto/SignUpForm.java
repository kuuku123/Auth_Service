package org.example.auth_service.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SignUpForm {

    @NotBlank
    @Length(min = 3, max = 20)
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 1, max = 50)
    private String password;
}
