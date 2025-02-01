package org.example.auth_service.config.security.common.converters;


import org.example.auth_service.config.security.ProviderUser;
import org.example.auth_service.config.security.common.enums.OAuth2Config;
import org.example.auth_service.config.security.common.util.OAuth2Utils;
import org.example.auth_service.config.security.social.GoogleUser;

public final class OAuth2GoogleProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        if (!providerUserRequest.clientRegistration().getRegistrationId().equals(OAuth2Config.SocialType.GOOGLE.getSocialName())) {
            return null;
        }

        return new GoogleUser(OAuth2Utils.getMainAttributes(
                providerUserRequest.oAuth2User()),
                providerUserRequest.oAuth2User(),
                providerUserRequest.clientRegistration());
    }
}
