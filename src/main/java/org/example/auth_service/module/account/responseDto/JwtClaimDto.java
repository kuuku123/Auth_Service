package org.example.auth_service.module.account.responseDto;

import lombok.Data;


@Data
public class JwtClaimDto {

    private String nickname;
    private String bio;
    private String url;
    private String occupation;
    private String location;
    private String email;
    private boolean emailVerified;
    private String profileImage;

}
