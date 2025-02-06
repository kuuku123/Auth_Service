package org.example.auth_service.security.config.auth_entity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthEntityUserService implements UserDetailsService {

    private final AuthEntityService authEntityService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthEntity authEntity = authEntityService.getAuthEntity(email);
        AuthEntityUser authEntityUser = new AuthEntityUser(authEntity);
        return authEntityUser;
    }
}
