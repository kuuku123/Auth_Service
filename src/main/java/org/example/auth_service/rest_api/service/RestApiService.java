package org.example.auth_service.rest_api.service;

import java.net.URI;
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
        appServerUrl + "/on-oauth-success", oAuth2Dto, String.class);

    HttpStatusCode statusCode = responseEntity.getStatusCode();
    System.out.println("HTTP Status Code: " + statusCode);

    // Extract the Location header
    URI redirectUri = responseEntity.getHeaders().getLocation();

    return "redirect:" + redirectUri;
  }

  public void fireSocialMergeAccount(OAuth2Dto oAuth2Dto) {
    restTemplate.postForEntity(appServerUrl + "/social/merge-account", oAuth2Dto, String.class);
  }

  public void fireSocialSeparateAccount(OAuth2Dto oAuth2Dto) {
    restTemplate.postForEntity(appServerUrl + "/social/separate-account", oAuth2Dto, String.class);
  }

}
