package org.example.auth_service.security.oauth2.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.security.ApiResponse;
import org.example.auth_service.security.config.auth_entity.AuthEntity;
import org.example.auth_service.security.config.auth_entity.AuthEntityService;
import org.example.auth_service.security.oauth2.service.SecurityService;
import org.example.auth_service.security.util.MyConstants;
import org.example.auth_service.security.util.MyUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialController {

  private final SecurityService securityService;
  private final AuthEntityService authEntityService;
  private final MyUtils myUtils;

  @GetMapping("/merge-account")
  public ResponseEntity<String> mergeAccounts(
    @RequestHeader(MyConstants.HEADER_USER_EMAIL) String email, HttpServletResponse response
  ) {
    AuthEntity authEntity = securityService.mergeAccount(email);
    String accessToken = authEntityService.createAccessToken(authEntity.getEmail());
    myUtils.putAccessTokenInCookie(response, accessToken);
    ApiResponse<String> apiResponse = new ApiResponse<>("account has merged", HttpStatus.OK,
      null);
    return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
  }

  @GetMapping("/separate-account")
  public ResponseEntity<String> separateAccounts(
    @RequestHeader(MyConstants.HEADER_USER_EMAIL) String email) {
    System.out.println("email = " + email);
    ApiResponse<String> apiResponse = new ApiResponse<>("account has separated", HttpStatus.OK,
      null);
    return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
  }

}
