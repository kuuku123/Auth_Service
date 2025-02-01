package org.example.auth_service.config.security.service;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.config.security.ProviderUser;
import org.example.auth_service.config.security.User;
import org.example.auth_service.config.security.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void register(String registrationId, ProviderUser providerUser) {

        User user = User.builder().registrationId(registrationId)
                .id(providerUser.getId())
                .username(providerUser.getUsername())
                .password(providerUser.getPassword())
                .authorities(providerUser.getAuthorities())
                .provider(providerUser.getProvider())
                .email(providerUser.getEmail())
                .picture(providerUser.getPicture())
                .build();

        userRepository.register(user);
    }
}
