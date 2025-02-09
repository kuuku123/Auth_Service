package org.example.auth_service.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class MyUtils {

  public void putAccessTokenInCookie(HttpServletResponse response, String accessToken) {
    Cookie tokenCookie = new Cookie("accessToken", accessToken);
    tokenCookie.setHttpOnly(
      true);
    tokenCookie.setSecure(
      true);
    tokenCookie.setPath("/");
    response.addCookie(tokenCookie);
  }


}
