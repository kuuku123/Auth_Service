package org.example.auth_service.security.config;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.security.oauth2.CustomAuthorizationRequestResolver;
import org.example.auth_service.security.oauth2.handler.OAuth2SuccessHandler;
import org.example.auth_service.security.oauth2.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final ClientRegistrationRepository clientRegistrationRepository;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.logout(AbstractHttpConfigurer::disable);

    http.oauth2Login(oauth2 -> oauth2
      .userInfoEndpoint(
        userInfoEndpointConfig -> userInfoEndpointConfig
          .userService(customOAuth2UserService))
      .authorizationEndpoint(authEndpoint -> authEndpoint.authorizationRequestResolver(
        new CustomAuthorizationRequestResolver(clientRegistrationRepository)))
      .successHandler(oAuth2SuccessHandler));  // OAuth2

    return http.build();
  }


  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
