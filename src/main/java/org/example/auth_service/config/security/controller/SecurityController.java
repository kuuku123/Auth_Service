package org.example.auth_service.config.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.config.security.PrincipalUser;
import org.example.auth_service.config.security.service.SecurityService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

    @GetMapping("/on-oauth-success")
    public String onSocialSuccess(@AuthenticationPrincipal PrincipalUser principalUser, HttpServletRequest request, HttpServletResponse response) {
        String url = securityService.chooseOptioncreateAccount(principalUser, request, response);
        return url;
    }
}
