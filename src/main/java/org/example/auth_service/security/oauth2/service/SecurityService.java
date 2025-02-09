package org.example.auth_service.security.oauth2.service;

import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.security.config.auth_entity.AuthEntity;
import org.example.auth_service.security.config.auth_entity.AuthEntityService;
import org.example.auth_service.security.dto.SignUpForm;
import org.example.auth_service.security.oauth2.dto.OAuth2Dto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SecurityService {

  @Value("${front.redirectUrl}")
  private String redirectUrl;

  private final AuthEntityService authEntityService;

  @Transactional
  public String chooseOptioncreateAccount(OAuth2Dto oAuth2Dto) {
    AuthEntity authEntity = null;
    try {
      authEntity = authEntityService.getAuthEntity(oAuth2Dto.getAttribute("email"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (authEntity != null) {
      String accessToken = authEntityService.createAccessToken(authEntity.getEmail());
      String mergedSocialProviders = authEntity.getCreatedOrMergedSocialProviders();
      if (!mergedSocialProviders.equals("")) {
        String[] providers = mergedSocialProviders.split(",");
        for (String provider : providers) {
          if (provider.equals(oAuth2Dto.getProvider())) {
            return "redirect:" + redirectUrl + "/already-merged-account" + " Bearer " + accessToken;
          }
        }
      }
      authEntity.setCreatedOrMergedSocialProviders(
        oAuth2Dto.getProvider()); // if already exist , will return by already merged account
      return "redirect:" + redirectUrl + "/merge-account" + " Bearer " + accessToken;
    } else {
      SignUpForm signUpForm = new SignUpForm();
      signUpForm.setNickname(oAuth2Dto.getAttribute("name"));
      signUpForm.setEmail(oAuth2Dto.getAttribute("email"));
      signUpForm.setPassword("");
      AuthEntity createdAuthEntity = authEntityService.saveAuthEntity(signUpForm);
      String createdOrMergedSocialProviders = createdAuthEntity.getCreatedOrMergedSocialProviders();
      createdOrMergedSocialProviders += "," + oAuth2Dto.getProvider();
      createdAuthEntity.setCreatedOrMergedSocialProviders(createdOrMergedSocialProviders);
      createdAuthEntity.setSubSocialIdentifier(new BigInteger(oAuth2Dto.getAttribute("sub")));
      createdAuthEntity.setEmailVerified(true);
      String accessToken = authEntityService.createAccessToken(oAuth2Dto.getAttribute("email"));

      return "redirect:" + redirectUrl + "/social-account-setPassword" + " Bearer " + accessToken;
    }
  }

  @Transactional
  public AuthEntity mergeAccount(String email
  ) {

    AuthEntity authEntity = authEntityService.getAuthEntity(email);
    authEntity.setEmailVerified(true);

    return authEntity;
  }
}
