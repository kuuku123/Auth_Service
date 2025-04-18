package org.example.auth_service.security.oauth2.social;

import org.example.auth_service.security.oauth2.Attributes;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class NaverUser extends OAuth2ProviderUser {

  public NaverUser(Attributes attributes, OAuth2User oAuth2User,
      ClientRegistration clientRegistration) {
    super(attributes.getSubAttributes(), oAuth2User, clientRegistration);
  }

  @Override
  public String getId() {
    return (String) getAttributes().get("id");
  }

  @Override
  public String getUsername() {
    return (String) getAttributes().get("name");
  }

  @Override
  public String getPicture() {
    return (String) getAttributes().get("profile_image");
  }
}