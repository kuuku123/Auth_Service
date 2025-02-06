package org.example.auth_service.security;

import lombok.Data;

@Data
public class JwtClaimDto {

    private String email;
    private String nickname;
}
