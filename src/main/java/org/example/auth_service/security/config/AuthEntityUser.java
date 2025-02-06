package org.example.auth_service.security.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthEntityUser implements UserDetails {

    private final AuthEntity authEntity;

    public AuthEntityUser(AuthEntity authEntity) {
        this.authEntity = authEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return authEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return "";
    }

    public String getEmail() {
        return authEntity.getEmail();
    }
}
