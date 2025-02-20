package org.example.auth_service.security.config.auth_entity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.rest_api.dto.SignUpRequest;
import org.example.auth_service.rest_api.service.RestApiService;
import org.example.auth_service.security.JwtClaimDto;
import org.example.auth_service.security.JwtUtils;
import org.example.auth_service.security.dto.LoginForm;
import org.example.auth_service.security.dto.SignUpForm;
import org.example.auth_service.security.exception.UserNotFoundException;
import org.example.auth_service.security.mail.EmailMessage;
import org.example.auth_service.security.mail.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthEntityService {

  @Value("${auth.server.url}")
  private String authServerUrl;

  private final AuthEntityRepository authUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;
  private final RestApiService restApiService;
  private final ModelMapper modelMapper;
  private final ExecutorService executorService = Executors.newCachedThreadPool();
  private final TemplateEngine templateEngine;
  private final EmailService emailService;

  @Transactional(noRollbackFor = UserNotFoundException.class)
  public AuthEntity getAuthEntity(String nicknameOrEmail) {
    AuthEntity authEntity = authUserRepository.findByEmail(nicknameOrEmail);
    if (authEntity == null) {
      authEntity = authUserRepository.findByNickname(nicknameOrEmail);
    }
    if (authEntity == null) {
      throw new UserNotFoundException("user not found");
    }
    return authEntity;
  }

  public AuthEntity saveAuthEntity(SignUpForm signUpForm) {
    AuthEntity authEntity = saveAuthEntityBySignUpForm(signUpForm);
    SignUpRequest signUpRequest = modelMapper.map(signUpForm, SignUpRequest.class);
    restApiService.fireSignUpRequest(signUpRequest);
    return authEntity;
  }

  private AuthEntity saveAuthEntityBySignUpForm(SignUpForm signUpForm) {
    AuthEntity authEntity = new AuthEntity();
    String nickname = signUpForm.getNickname();
    String email = signUpForm.getEmail();
    String password = signUpForm.getPassword();
    String encode = passwordEncoder.encode(password);
    authEntity.setNickname(nickname);
    authEntity.setEmail(email);
    authEntity.setPassword(encode);
    authEntity.generateEmailCheckToken();
    authUserRepository.save(authEntity);
    return authEntity;
  }


  public String login(LoginForm loginForm) {
    AuthEntity authEntity = getAuthEntity(loginForm.getNicknameOrEmail());
    String dbPassword = authEntity.getPassword();
    if (!passwordEncoder.matches(loginForm.getPassword(), dbPassword)) {
      throw new SecurityException();
    }
    return createAccessToken(loginForm.getNicknameOrEmail());
  }

  public String createAccessToken(String email) {
    JwtClaimDto jwtClaimDto = new JwtClaimDto();
    jwtClaimDto.setEmail(email);
    return jwtUtils.createAccessToken(jwtClaimDto);
  }

  public void sendSignupConfirmEmail(AuthEntity newAuthEntity) {

    Context context = new Context();
    context.setVariable("link",
      "/check-email-token?token=" + newAuthEntity.getEmailCheckToken() + "&email="
        + newAuthEntity.getEmail());
    context.setVariable("nickname", newAuthEntity.getNickname());
    context.setVariable("linkName", "Email Verification");
    context.setVariable("message", "Click the link to use the Study Cafe service.");
    context.setVariable("host", authServerUrl);

    executorService.submit(() -> {
      String message = templateEngine.process("email/simple-link", context);

      EmailMessage emailMessage = EmailMessage.builder()
        .to(newAuthEntity.getEmail())
        .from("tonydevpc123@gmail.com")
        .subject("Study Cafe , SignUp Verification")
        .message(message)
        .build();
      emailService.sendEmail(emailMessage);
    });
  }

  public void updatePassword(String email, String password) {
    getAuthEntity(email).setPassword(passwordEncoder.encode(password));
  }
}
