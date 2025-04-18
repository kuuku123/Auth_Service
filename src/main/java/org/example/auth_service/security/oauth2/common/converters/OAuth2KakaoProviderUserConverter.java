package org.example.auth_service.security.oauth2.common.converters;

import org.example.auth_service.security.oauth2.ProviderUser;
import org.example.auth_service.security.oauth2.common.enums.OAuth2Config;
import org.example.auth_service.security.oauth2.common.util.OAuth2Utils;
import org.example.auth_service.security.oauth2.social.KakaoUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public final class OAuth2KakaoProviderUserConverter implements
    ProviderUserConverter<ProviderUserRequest, ProviderUser> {

  @Override
  public ProviderUser convert(ProviderUserRequest providerUserRequest) {

    if (!providerUserRequest.clientRegistration().getRegistrationId()
        .equals(OAuth2Config.SocialType.KAKAO.getSocialName())) {
      return null;
    }

    if (providerUserRequest.oAuth2User() instanceof OidcUser) {
      return null;
    }

    return new KakaoUser(OAuth2Utils.getOtherAttributes(
        providerUserRequest.oAuth2User(), "kakao_account", "profile"),
        providerUserRequest.oAuth2User(),
        providerUserRequest.clientRegistration());
  }
}
