package org.example.auth_service.security.dto;

import lombok.Data;

@Data
public class PasswordForm {

  private String newPassword;

  private String newPasswordConfirm;
}
