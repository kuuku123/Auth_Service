package org.example.auth_service.module.account.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth_service.config.AppProperties;
import org.example.auth_service.config.mail.EmailMessage;
import org.example.auth_service.config.mail.EmailService;
import org.example.auth_service.module.account.UserAccount;
import org.example.auth_service.module.account.domain.Account;
import org.example.auth_service.module.account.form.LoginForm;
import org.example.auth_service.module.account.form.Notifications;
import org.example.auth_service.module.account.form.Profile;
import org.example.auth_service.module.account.form.SignUpForm;
import org.example.auth_service.module.account.repository.AccountRepository;
import org.example.auth_service.module.account.responseDto.JwtClaimDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    private final RememberMeServices rememberMeServices;
    private final PersistentTokenRepository persistentTokenRepository;
    private final UserDetailsService userDetailsService;
    private final ModelMapper modelMapper;
    private final SecurityContextRepository securityContextRepository;

    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @PostConstruct
    public void init() {
        authenticationProvider.setUserDetailsService(userDetailsService);
    }

    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        sendSignupConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {

        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();

        ClassPathResource imgFile = new ClassPathResource("static/images/anonymous.JPG");
        try (InputStream inputStream = imgFile.getInputStream()) {
            byte[] anonymousProfileJpg = inputStream.readAllBytes();
            account.setProfileImage(anonymousProfileJpg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return accountRepository.save(account);
    }

    public void sendSignupConfirmEmail(Account newAccount) {

        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "Email Verification");
        context.setVariable("message", "Click the link to use the Study Cafe service.");
        context.setVariable("host", appProperties.getHost());

        executorService.submit(() -> {
            String message = templateEngine.process("email/simple-link", context);

            EmailMessage emailMessage = EmailMessage.builder()
                    .to(newAccount.getEmail())
                    .from("tonydevpc123@gmail.com")
                    .subject("Study Cafe , SignUp Verification")
                    .message(message)
                    .build();
            emailService.sendEmail(emailMessage);
        });

    }

    // Storing the Authentication manually
    public Account login(LoginForm loginForm, HttpServletRequest request, HttpServletResponse response) {
        String nicknameOrEmail = loginForm.getNicknameOrEmail();
        Account account = getAccount(nicknameOrEmail);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), loginForm.getPassword());
        authenticationProvider.authenticate(token);

        saveAuthentication(request, response, account, loginForm.getPassword(), false);

        return account;
    }


    public void logout(Account account, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        persistentTokenRepository.removeUserTokens(account.getNickname());
    }

    public void saveAuthentication(HttpServletRequest request, HttpServletResponse response, Account account, String password, boolean alreadyEncoded) {
        String encodedPassword = null;
        if (alreadyEncoded) {
            encodedPassword = password;
        } else {
            encodedPassword = passwordEncoder.encode(password);
        }
        UsernamePasswordAuthenticationToken authorizedToken = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), encodedPassword, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authorizedToken);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
        rememberMeServices.loginSuccess(request, response, authorizedToken);
    }

    public void signUp(Account account, HttpServletRequest request, HttpServletResponse response) {
        saveAuthentication(request, response, account, account.getPassword(), false);
    }

    public void completeSignUp(Account account, HttpServletRequest request, HttpServletResponse response) {
        account.completeSignUp();
        signUp(account, request, response);
    }

    public String getProfileImage(Account account) {
        Account byNickname = accountRepository.findByNickname(account.getNickname());
        byte[] profileImage = byNickname.getProfileImage();
        String encodedImage = org.apache.tomcat.util.codec.binary.Base64.encodeBase64String(profileImage);
        return encodedImage;
    }


    public void updateProfile(Account account, Profile profile) {
        // Remove the data URL prefix if it exists
        String base64Image = profile.getProfileImage();
        if (base64Image != null) {
            if (base64Image.startsWith("data:image/jpeg;base64,")) {
                base64Image = base64Image.substring("data:image/jpeg;base64,".length());
            }
            if (base64Image.startsWith("data:image/png;base64,")) {
                base64Image = base64Image.substring("data:image/png;base64,".length());
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            account.setProfileImage(imageBytes);
            modelMapper.map(profile, account);
            accountRepository.save(account); // merge detached entity
        }
    }

    public JwtClaimDto updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        Account updatedAccount = accountRepository.save(account);
        return modelMapper.map(updatedAccount, JwtClaimDto.class);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname, HttpServletRequest request, HttpServletResponse response) {
        account.setNickname(nickname);
        accountRepository.save(account);

        //TODO just for compile
        LoginForm loginForm = new LoginForm();
        login(loginForm, request, response);
    }

    public void sendLoginLink(Account account) {

        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "스터디 카페 로그인하기");
        context.setVariable("message", "로그인 하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        executorService.submit(() -> {
            String message = templateEngine.process("email/simple-link", context);
            EmailMessage emailMessage = EmailMessage.builder()
                    .to(account.getEmail())
                    .subject("스터디 카페 , 로그인 링크")
                    .message(message)
                    .build();
            emailService.sendEmail(emailMessage);
        });
    }


    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public Account getAccount(String nicknameOrEmail) {
        Account account = accountRepository.findByNickname(nicknameOrEmail);
        if (account == null) {
            account = accountRepository.findByEmail(nicknameOrEmail);
            if (account == null) {
                log.error(nicknameOrEmail + "에 해당하는 사용자가 없습니다.");
                throw new IllegalArgumentException(nicknameOrEmail + "에 해당하는 사용자가 없습니다.");
            }
        }
        return account;
    }

    public JwtClaimDto getJwtClaimDto(Account account) {
        JwtClaimDto jwtClaimDto = modelMapper.map(account, JwtClaimDto.class);
        return jwtClaimDto;
    }
}
