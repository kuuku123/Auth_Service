package org.example.auth_service.security.oauth2;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attributes {

  private Map<String, Object> mainAttributes;
  private Map<String, Object> subAttributes;
  private Map<String, Object> otherAttributes;

}
