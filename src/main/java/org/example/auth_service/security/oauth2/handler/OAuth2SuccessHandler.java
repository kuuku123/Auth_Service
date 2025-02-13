package org.example.auth_service.security.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth_service.rest_api.service.RestApiService;
import org.example.auth_service.security.oauth2.dto.OAuth2Dto;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final RestApiService restApiService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    Authentication authentication) throws IOException, ServletException {

    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

    // Get the OAuth2User for other attributes like email and name
    OAuth2User oAuth2User = oauthToken.getPrincipal();
    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");
    String sub = oAuth2User.getAttribute("sub");

    HashMap<String, String> attributes = new HashMap<>();
    attributes.put("email", email);
    attributes.put("name", name);
    attributes.put("sub", sub);

    // Retrieve the provider's registration ID (e.g., "google")
    String provider = oauthToken.getAuthorizedClientRegistrationId();

    OAuth2Dto oAuth2Dto = new OAuth2Dto();
    oAuth2Dto.setAttributes(attributes);
    oAuth2Dto.setProvider(provider);

    log.info("trying to call fireSocialLoginRequest");
    String redirectUrl = restApiService.fireSocialLoginRequest(oAuth2Dto);
    log.info("redirect to " + redirectUrl);

    if (redirectUrl != null && redirectUrl.startsWith("redirect:")) {
      redirectUrl = redirectUrl.substring("redirect:".length());
    }

    // Extract the access token if present and remove it from the URL
    String accessToken = null;
    if (redirectUrl != null && redirectUrl.contains("Bearer ")) {
      String[] parts = redirectUrl.split("Bearer ");
      // The first part is the URL without the access token.
      redirectUrl = parts[0].trim();
      // The second part (if present) is the access token.
      if (parts.length > 1) {
        accessToken = parts[1].trim();
      }
    }

    if (redirectUrl != null) {
      redirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
        .queryParam("email", email)
        .build().toUriString();
    }
    // If an access token was extracted, set it in an HTTP-only, secure cookie.
    if (accessToken != null) {
      Cookie tokenCookie = new Cookie("accessToken", accessToken);
      tokenCookie.setHttpOnly(
        true);      // Prevents client-side JavaScript from accessing the cookie
      tokenCookie.setSecure(
        false);        // Ensure the cookie is sent over HTTPS only (if using HTTPS)
      tokenCookie.setPath("/");           // Adjust the path as needed for your application
      // Optionally, you can set a max age: tokenCookie.setMaxAge(60 * 15); // 15 minutes, for example
      response.addCookie(tokenCookie);
    }

    if (redirectUrl != null) {
      getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    } else {
      super.onAuthenticationSuccess(request, response, authentication);
    }
  }
}
