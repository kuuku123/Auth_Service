package org.example.auth_service.config.security.certification;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.config.security.ProviderUser;
import org.example.auth_service.config.security.User;
import org.example.auth_service.config.security.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelfCertification {

    private final UserRepository userRepository;

    public void checkCertification(ProviderUser providerUser) {
        User user = userRepository.findByUsername(providerUser.getId());
//        if(user != null) {
        boolean bool = providerUser.getProvider().equals("none") || providerUser.getProvider().equals("naver");
        providerUser.isCertificated(bool);
//        }
    }

    public void certificate(ProviderUser providerUser) {

    }
}
