package org.example.auth_service.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginForm {

    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 1, max = 50)
    private String password;
}
