package org.example.auth_service.rest_api.service;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.rest_api.dto.SignUpRequest;
import org.example.auth_service.security.dto.LoginForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestApiService {

    private final RestTemplate restTemplate;

    @Value("${app.server.url}")
    private String appServerUrl;


    public RestApiService() {
        this.restTemplate = new RestTemplate();
    }

    public void fireSignUpRequest(SignUpRequest signUpRequest) {
        ResponseEntity<String> signUpResponse = restTemplate.postForEntity(appServerUrl+"/sign-up", signUpRequest, String.class);
    }
}
