package org.example.auth_service.security.oauth2.dto;

import java.util.Map;
import lombok.Data;

@Data
public class OAuth2Dto {

  private String provider;
  private Map<String, String> attributes;

  public String getAttribute(String key) {
    return attributes.get(key);
  }


}
