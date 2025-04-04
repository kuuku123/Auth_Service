package org.example.auth_service.security.config.auth_entity.dto;

import lombok.Data;

@Data
public class EmailVerificationDto {
  private String email;
  private String code;
}
