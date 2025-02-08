package org.example.auth_service.security.oauth2.common.converters;


import org.example.auth_service.security.oauth2.ProviderUser;
import org.example.auth_service.security.oauth2.common.enums.OAuth2Config;
import org.example.auth_service.security.oauth2.common.util.OAuth2Utils;
import org.example.auth_service.security.oauth2.social.NaverUser;

public final class OAuth2NaverProviderUserConverter implements
    ProviderUserConverter<ProviderUserRequest, ProviderUser> {

  @Override
  public ProviderUser convert(ProviderUserRequest providerUserRequest) {

    if (!providerUserRequest.clientRegistration().getRegistrationId()
        .equals(OAuth2Config.SocialType.NAVER.getSocialName())) {
      return null;
    }

    return new NaverUser(OAuth2Utils.getSubAttributes(
        providerUserRequest.oAuth2User(), "response"),
        providerUserRequest.oAuth2User(),
        providerUserRequest.clientRegistration());
  }
}
