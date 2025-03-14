package org.example.auth_service.security.oauth2.common.enums;

public class OAuth2Config {

  public enum SocialType {
    GOOGLE("google"),
    APPLE("apple"),
    FACEBOOK("facebook"),
    NAVER("naver"),
    KAKAO("kakao");
    private final String socialName;

    private SocialType(String socialName) {
      this.socialName = socialName;
    }

    public String getSocialName() {
      return socialName;
    }
  }
}
