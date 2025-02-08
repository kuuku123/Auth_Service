package org.example.auth_service.rest_api.dto;

import lombok.Data;

@Data
public class SignUpRequest {

    private String nickname;
    private String email;
    private String password;

}
