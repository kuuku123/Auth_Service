package org.example.auth_service.security.config.auth_entity;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.rest_api.dto.SignUpRequest;
import org.example.auth_service.rest_api.service.RestApiService;
import org.example.auth_service.security.JwtClaimDto;
import org.example.auth_service.security.JwtUtils;
import org.example.auth_service.security.dto.LoginForm;
import org.example.auth_service.security.dto.SignUpForm;
import org.example.auth_service.security.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthEntityService {

    private final AuthEntityRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RestApiService restApiService;
    private final ModelMapper modelMapper;

    public AuthEntity getAuthEntity(String nicknameOrEmail) {
        AuthEntity authEntity = authUserRepository.findByEmail(nicknameOrEmail);
        if (authEntity== null) {
            authEntity = authUserRepository.findByNickname(nicknameOrEmail);
        }
        if (authEntity == null) {
            throw new UserNotFoundException("user not found");
        }
        return authEntity;
    }

    public String saveAuthEntity(SignUpForm signUpForm) {
        String email = saveAuthEntityBySignUpForm(signUpForm);
        String accessToken = createAccessToken(email);

        SignUpRequest signUpRequest = modelMapper.map(signUpForm, SignUpRequest.class);
        restApiService.fireSignUpRequest(signUpRequest);

        return accessToken;
    }

    private String saveAuthEntityBySignUpForm(SignUpForm signUpForm) {
        AuthEntity authEntity = new AuthEntity();
        String nickname = signUpForm.getNickname();
        String email = signUpForm.getEmail();
        String password = signUpForm.getPassword();
        String encode = passwordEncoder.encode(password);
        authEntity.setNickname(nickname);
        authEntity.setEmail(email);
        authEntity.setPassword(encode);
        authUserRepository.save(authEntity);
        return email;
    }

    public String login(LoginForm loginForm) {
        AuthEntity authEntity = getAuthEntity(loginForm.getNicknameOrEmail());
        String dbPassword = authEntity.getPassword();
        if (!passwordEncoder.matches(loginForm.getPassword(), dbPassword)) {
            return null;
        }
        return createAccessToken(loginForm.getNicknameOrEmail());
    }

    private String createAccessToken(String email) {
        JwtClaimDto jwtClaimDto = new JwtClaimDto();
        jwtClaimDto.setEmail(email);
        return jwtUtils.createAccessToken(jwtClaimDto);
    }

}
