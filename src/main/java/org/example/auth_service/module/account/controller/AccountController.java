package org.example.auth_service.module.account.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.config.security.JwtUtils;
import org.example.auth_service.module.account.CurrentAccount;
import org.example.auth_service.module.account.domain.Account;
import org.example.auth_service.module.account.form.LoginForm;
import org.example.auth_service.module.account.form.SignUpForm;
import org.example.auth_service.module.account.repository.AccountRepository;
import org.example.auth_service.module.account.responseDto.JwtClaimDto;
import org.example.auth_service.module.account.responseDto.ApiResponse;
import org.example.auth_service.module.account.service.AccountService;
import org.example.auth_service.module.account.validator.SignUpFormValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final JwtUtils jwtUtils;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/xsrf-token")
    public ResponseEntity<String> xsrfToken() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/login", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<String> login(@RequestBody LoginForm loginForm, HttpServletRequest request, HttpServletResponse response) {
        Account account = accountService.login(loginForm, request, response);
        JwtClaimDto jwtClaimDto = accountService.getJwtClaimDto(account);
        String accessToken = jwtUtils.createAccessToken(jwtClaimDto);
        ApiResponse<JwtClaimDto> apiResponse = new ApiResponse<>("login succeed", HttpStatus.OK, jwtClaimDto, accessToken);
        return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
    }


    @GetMapping("/logout")
    public ResponseEntity<String> logout(@CurrentAccount Account account, HttpServletRequest request, HttpServletResponse response) {
        accountService.logout(account, request);
        ApiResponse<String> apiResponse = new ApiResponse<>("logout succeed", HttpStatus.OK, null, null);
        return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUpSubmit(@Valid @RequestBody SignUpForm signUpForm, Errors errors, HttpServletRequest request, HttpServletResponse response) {
        if (errors.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : errors.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            ApiResponse<Map<String, String>> signupFailed = new ApiResponse<>("signup failed", HttpStatus.BAD_REQUEST, errorMap, null);
            return new ResponseEntity<>(new Gson().toJson(signupFailed), HttpStatus.BAD_REQUEST);
        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.signUp(account, request, response);
        JwtClaimDto jwtClaimDto = accountService.getJwtClaimDto(account);
        String accessToken = jwtUtils.createAccessToken(jwtClaimDto);

        ApiResponse<JwtClaimDto> apiResponse = new ApiResponse<>("sign up succeed", HttpStatus.OK, jwtClaimDto,accessToken);
        return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
    }


    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model, HttpServletRequest request, HttpServletResponse response) {
        Account account = accountRepository.findByEmail(email);
        String view = "email/checked-email";
        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if (!account.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }

        accountService.completeSignUp(account, request, response);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "email/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public ResponseEntity<String> resendConfirmEmail(@CurrentAccount Account account) {
        if (account.canSendConfirmationEmail()) {
            ApiResponse<String> apiResponse = new ApiResponse<>("The retransmission cycle is 1 hour.", HttpStatus.BAD_REQUEST, null,null);
            return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.BAD_REQUEST);
        }
        account.generateEmailCheckToken();
        accountService.sendSignupConfirmEmail(account);
        ApiResponse<String> apiResponse = new ApiResponse<>("resend succeed.", HttpStatus.OK, null, null);
        return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
    }


    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model, HttpServletRequest request, HttpServletResponse response) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";
        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "로그인할 수 없습니다.");
            return view;
        }

        //TODO just for compile
        LoginForm loginForm = new LoginForm();
        accountService.login(loginForm, request, response);
        return view;
    }
}
