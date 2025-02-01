package org.example.auth_service.config.security.service;

import lombok.Getter;
import org.example.auth_service.config.security.ProviderUser;
import org.example.auth_service.config.security.User;
import org.example.auth_service.config.security.certification.SelfCertification;
import org.example.auth_service.config.security.common.converters.ProviderUserConverter;
import org.example.auth_service.config.security.common.converters.ProviderUserRequest;
import org.example.auth_service.config.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;

@Service
@Getter
public abstract class AbstractOAuth2UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SelfCertification certification;
    @Autowired
    private ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    public void selfCertificate(ProviderUser providerUser) {
        certification.checkCertification(providerUser);
    }

    public void register(ProviderUser providerUser, OAuth2UserRequest userRequest) {

        User user = userRepository.findByUsername(providerUser.getUsername());

        if (user == null) {
            ClientRegistration clientRegistration = userRequest.getClientRegistration();
            //TODO need to use Account
//            userService.register(clientRegistration.getRegistrationId(), providerUser);
        } else {
            System.out.println("userRequest = " + userRequest);
        }
    }

    public ProviderUser providerUser(ProviderUserRequest providerUserRequest) {
        return providerUserConverter.convert(providerUserRequest);
    }
}
