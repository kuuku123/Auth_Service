package org.example.auth_service.security.oauth2.common.converters;

import org.example.auth_service.security.oauth2.User;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public record ProviderUserRequest(ClientRegistration clientRegistration, OAuth2User oAuth2User,
                                  User user) {

  public ProviderUserRequest(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
    this(clientRegistration, oAuth2User, null);
  }

  ;

  public ProviderUserRequest(User user) {
    this(null, null, user);
  }

  ;
}
