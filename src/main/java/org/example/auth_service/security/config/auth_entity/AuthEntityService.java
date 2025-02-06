package org.example.auth_service.security.config.auth_entity;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.rest_api.dto.SignUpRequest;
import org.example.auth_service.rest_api.service.RestApiService;
import org.example.auth_service.security.JwtClaimDto;
import org.example.auth_service.security.JwtUtils;
import org.example.auth_service.security.dto.SignUpForm;
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

    public AuthEntity getAuthEntity(String email) {
        return authUserRepository.findByEmail(email);
    }

    public String saveAuthEntity(SignUpForm signUpForm) {
        AuthEntity authEntity = new AuthEntity();
        String nickname = signUpForm.getNickname();
        String email = signUpForm.getEmail();
        String password = signUpForm.getPassword();
        String encode = passwordEncoder.encode(password);
        authEntity.setNickname(nickname);
        authEntity.setEmail(email);
        authEntity.setPassword(encode);
        authUserRepository.save(authEntity);

        JwtClaimDto jwtClaimDto = new JwtClaimDto();
        jwtClaimDto.setEmail(email);
        String accessToken = jwtUtils.createAccessToken(jwtClaimDto);

        SignUpRequest signUpRequest = modelMapper.map(signUpForm, SignUpRequest.class);
        restApiService.fireSignUpRequest(signUpRequest);

        return accessToken;
    }

}
