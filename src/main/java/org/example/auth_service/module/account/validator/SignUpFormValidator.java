package org.example.auth_service.module.account.validator;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.module.account.form.SignUpForm;
import org.example.auth_service.module.account.repository.AccountRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) target;
        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email","invalid.email",new Object[]{signUpForm.getEmail()},"이미 사용중인 이메일입니다.");
        }
        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname","invalid.nickname",new Object[]{signUpForm.getNickname()}, "이미 사용중인 넥네임입니다.");
        }
    }
}
