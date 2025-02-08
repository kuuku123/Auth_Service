package org.example.auth_service.security;


import com.google.gson.Gson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.security.config.auth_entity.AuthEntityService;
import org.example.auth_service.security.dto.LoginForm;
import org.example.auth_service.security.dto.SignUpForm;
import org.example.auth_service.security.validator.SignUpFormValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final AuthEntityService authEntityService;
    private final SignUpFormValidator signUpFormValidator;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }


    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpForm signUpForm , Errors errors) {
        if (errors.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : errors.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            ApiResponse<Map<String, String>> signupFailed = new ApiResponse<>("signup failed", HttpStatus.BAD_REQUEST, errorMap);
            return new ResponseEntity<>(new Gson().toJson(signupFailed), HttpStatus.BAD_REQUEST);
        }

        String accessToken = authEntityService.saveAuthEntity(signUpForm);
        ApiResponse<String> apiResponse = new ApiResponse<>("sign up succeed", HttpStatus.OK, accessToken);
        return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginForm loginForm) {
        String accessToken = authEntityService.login(loginForm);
        ApiResponse<String> apiResponse = new ApiResponse<>("login succeed", HttpStatus.OK, accessToken);
        return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
    }
}
