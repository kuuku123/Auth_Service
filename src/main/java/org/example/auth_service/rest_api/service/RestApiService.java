package org.example.auth_service.rest_api.service;

import org.example.auth_service.rest_api.dto.SignUpRequest;
import org.example.auth_service.security.oauth2.dto.OAuth2Dto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestApiService {

  private final RestTemplate restTemplate;

  @Value("${app.server.url}")
  private String appServerUrl;

  @Value("${auth.server.url}")
  private String authServerUrl;


  public RestApiService() {
    this.restTemplate = new RestTemplate();
  }

  public void fireSignUpRequest(SignUpRequest signUpRequest) {
    ResponseEntity<String> signUpResponse = restTemplate.postForEntity(appServerUrl + "/sign-up",
      signUpRequest, String.class);
  }

  public String fireSocialLoginRequest(OAuth2Dto oAuth2Dto) {
    // Send the POST request and get the response
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
      authServerUrl + "/on-oauth-success", oAuth2Dto, String.class);

    HttpStatusCode statusCode = responseEntity.getStatusCode();
    System.out.println("HTTP Status Code: " + statusCode);

    // Extract the Location header
    String redirectUri = responseEntity.getHeaders().getFirst("Location");

    return "redirect:" + redirectUri;
  }
}
