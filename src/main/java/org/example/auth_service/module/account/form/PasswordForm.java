package org.example.auth_service.module.account.form;

import lombok.Data;

@Data
public class PasswordForm {

    private String newPassword;

    private String newPasswordConfirm;
}
