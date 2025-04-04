package org.example.auth_service.security;


import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.security.config.auth_entity.AuthEntity;
import org.example.auth_service.security.config.auth_entity.AuthEntityRepository;
import org.example.auth_service.security.config.auth_entity.AuthEntityService;
import org.example.auth_service.security.config.auth_entity.dto.EmailVerificationDto;
import org.example.auth_service.security.config.auth_entity.dto.EmailVerifyRequestDto;
import org.example.auth_service.security.dto.LoginForm;
import org.example.auth_service.security.dto.PasswordForm;
import org.example.auth_service.security.dto.SignUpForm;
import org.example.auth_service.security.oauth2.dto.OAuth2Dto;
import org.example.auth_service.security.oauth2.service.SecurityService;
import org.example.auth_service.security.util.MyConstants;
import org.example.auth_service.security.util.MyUtils;
import org.example.auth_service.security.validator.SignUpFormValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequiredArgsConstructor
public class SecurityController {

  private final AuthEntityService authEntityService;
  private final SignUpFormValidator signUpFormValidator;
  private final SecurityService securityService;
  private final AuthEntityRepository authEntityRepository;

  @InitBinder("signUpForm")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(signUpFormValidator);
  }


  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@Valid @RequestBody SignUpForm signUpForm, Errors errors,
    HttpServletResponse response) {
    if (errors.hasErrors()) {
      Map<String, String> errorMap = new HashMap<>();
      for (FieldError error : errors.getFieldErrors()) {
        errorMap.put(error.getField(), error.getDefaultMessage());
      }
      ApiResponse<Map<String, String>> signupFailed = new ApiResponse<>("signup failed",
        HttpStatus.BAD_REQUEST, errorMap);
      return new ResponseEntity<>(new Gson().toJson(signupFailed), HttpStatus.BAD_REQUEST);
    }

    AuthEntity authEntity = authEntityService.saveAuthEntity(signUpForm);
    String accessToken = authEntityService.createAccessToken(authEntity.getEmail());
    authEntityService.sendSignupConfirmEmail(authEntity);
    MyUtils.putAccessTokenInCookie(response, accessToken);

    ApiResponse<String> apiResponse = new ApiResponse<>("sign up succeed", HttpStatus.OK,
      null);
    return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
  }


  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginForm loginForm,
    HttpServletResponse response) {
    String accessToken = authEntityService.login(loginForm);
    MyUtils.putAccessTokenInCookie(response, accessToken);
    ApiResponse<String> apiResponse = new ApiResponse<>("login succeed", HttpStatus.OK,
      null);
    return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(
    HttpServletResponse response) {
    MyUtils.putAccessTokenInCookie(response, null);
    ApiResponse<String> apiResponse = new ApiResponse<>("logout succeed", HttpStatus.OK,
      null);
    return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
  }


  @PostMapping("/update-password")
  public ResponseEntity<String> updatePassword(
    @RequestHeader(MyConstants.HEADER_USER_EMAIL) String email,
    @RequestBody PasswordForm passwordForm) {
    authEntityService.updatePassword(email, passwordForm.getNewPassword());
    ApiResponse<String> apiResponse = new ApiResponse<>("password update succeed", HttpStatus.OK,
      null);
    return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
  }

  @PostMapping("/on-oauth-success") // due to dependency cycle
  public String onSocialSuccess(@RequestBody OAuth2Dto oAuth2Dto) {
    String url = securityService.chooseOptioncreateAccount(oAuth2Dto);
    return url;
  }

  @PostMapping("/check-and-make-email-verification-code")
  public ResponseEntity<String> checkAndMakeEmailVerificationCode(@RequestBody
    EmailVerifyRequestDto emailVerifyRequestDto) {
    AuthEntity authEntity = authEntityService.checkAuthEntity(emailVerifyRequestDto.getEmail());
    if (authEntity != null) {
      ApiResponse<String> apiResponse = new ApiResponse<>("user with this email exist", HttpStatus.BAD_REQUEST,
        null);
      return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.BAD_REQUEST);
    } else {
      ApiResponse<String> apiResponse = new ApiResponse<>("email verification code generated", HttpStatus.OK,
        null);
      return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
    }
  }

  @PostMapping("/verify-email")
  public ResponseEntity<String> emailVerify(@RequestBody EmailVerificationDto emailVerificationDto) {
    boolean verified = authEntityService.validateEmailVerificationCode(emailVerificationDto);
    if (verified) {
      ApiResponse<String> apiResponse = new ApiResponse<>("email verified succeed", HttpStatus.OK,
        null);
      return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
    } else {
      ApiResponse<String> apiResponse = new ApiResponse<>("email verified failed", HttpStatus.BAD_REQUEST,
        null);
      return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.BAD_REQUEST);
    }
  }

//  @GetMapping("/resend-confirm-email")
//  public ResponseEntity<String> resendConfirmEmail(
//    @RequestHeader(MyConstants.HEADER_USER_EMAIL) String email) {
//    AuthEntity authEntity = authEntityService.getAuthEntity(email);
//    authEntity.generateEmailCheckToken();
//    authEntityService.sendSignupConfirmEmail(authEntity);
//    ApiResponse<String> apiResponse = new ApiResponse<>("resend succeed", HttpStatus.OK,
//      null);
//    return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
//  }
//
//  @GetMapping("/check-email-token")
//  public String checkEmailToken(String token, String email, Model model, HttpServletRequest request,
//    HttpServletResponse response) {
//    AuthEntity authEntity = authEntityService.getAuthEntity(email);
//    String view = "email/checked-email";
//    if (authEntity == null) {
//      model.addAttribute("error", "wrong.email");
//      return view;
//    }
//
//    if (!authEntity.isValidToken(token)) {
//      model.addAttribute("error", "wrong.token");
//      return view;
//    }
//
//    authEntity.completeSignUp();
//    authEntityRepository.save(authEntity);
//    model.addAttribute("numberOfUser", authEntityRepository.count());
//    model.addAttribute("nickname", authEntity.getNickname());
//    return view;
//  }
//
//  @GetMapping("/check-email-verified")
//  public ResponseEntity<String> checkEmailVerified(
//    @RequestHeader(MyConstants.HEADER_USER_EMAIL) String email) {
//    AuthEntity authEntity = authEntityService.getAuthEntity(email);
//    boolean emailVerified = authEntity.isEmailVerified();
//    ApiResponse<Boolean> apiResponse = new ApiResponse<>("email verified check succeed",
//      HttpStatus.OK,
//      emailVerified);
//    return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
//  }


}
