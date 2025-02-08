package org.example.auth_service.security.oauth2.service;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.security.oauth2.PrincipalUser;
import org.example.auth_service.security.oauth2.ProviderUser;
import org.example.auth_service.security.oauth2.User;
import org.example.auth_service.security.oauth2.common.converters.ProviderUserRequest;
import org.example.auth_service.security.oauth2.repository.UserRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

//@Service
@RequiredArgsConstructor
public class CustomUserDetailsService extends AbstractOAuth2UserService implements
    UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = userRepository.findByUsername(username);

    if (user == null) {
      user = User.builder()
          .id("1")
          .username("onjsdnjs")
          .password("{noop}1234")
          .authorities(AuthorityUtils.createAuthorityList("ROLE_USER"))
          .email("onjsdnjs@gmail.com")
          .build();
    }

    ProviderUserRequest providerUserRequest = new ProviderUserRequest(user);
    ProviderUser providerUser = providerUser(providerUserRequest);

    selfCertificate(providerUser);

    return new PrincipalUser(providerUser);
  }
}

