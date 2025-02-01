package org.example.auth_service.config.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.config.security.PrincipalUser;
import org.example.auth_service.module.account.domain.Account;
import org.example.auth_service.module.account.form.SignUpForm;
import org.example.auth_service.module.account.responseDto.JwtClaimDto;
import org.example.auth_service.module.account.service.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class SecurityService {

    @Value("${cors.allowed-origins}")
    private String allowedOrigin;

    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @Transactional
    public String chooseOptioncreateAccount(PrincipalUser principalUser, HttpServletRequest request, HttpServletResponse response) {
        Account account = null;
        try {
            account = accountService.getAccount(principalUser.getAttribute("email"));
        } catch (Exception e) {
        }
        if (account != null) {
            String mergedSocialProviders = account.getCreatedOrMergedSocialProviders();
            if (mergedSocialProviders != null) {
                String[] providers = mergedSocialProviders.split(",");
                for (String provider : providers) {
                    if (provider.equals(principalUser.providerUser().getProvider())) {
                        accountService.saveAuthentication(request, response, account, account.getPassword(), true);
                        return "redirect:" + allowedOrigin + "/already-merged-account";
                    }
                }
            }
            return "redirect:" + allowedOrigin + "/merge-account";
        } else {
            SignUpForm signUpForm = new SignUpForm();
            signUpForm.setNickname(principalUser.getAttribute("name"));
            signUpForm.setEmail(principalUser.getAttribute("email"));
            signUpForm.setPassword(principalUser.getPassword());
            Account createdAccount = accountService.processNewAccount(signUpForm);
            String createdOrMergedSocialProviders = createdAccount.getCreatedOrMergedSocialProviders();
            createdOrMergedSocialProviders += "," + principalUser.providerUser().getProvider();
            createdAccount.setCreatedOrMergedSocialProviders(createdOrMergedSocialProviders);
            accountService.saveAuthentication(request, response, createdAccount, createdAccount.getPassword(), true);

            return "redirect:" + allowedOrigin + "/social-account-setPassword";
        }
    }

    @Transactional
    public JwtClaimDto mergeAccount(PrincipalUser principalUser, HttpServletRequest request, HttpServletResponse response) {
        String sub = principalUser.getAttribute("sub");
        Account account = accountService.getAccount(principalUser.getAttribute("email"));
        BigInteger subSocialIdentifier = new BigInteger(sub);
        account.setSubSocialIdentifier(subSocialIdentifier);

        String createdOrMergedSocialProviders = account.getCreatedOrMergedSocialProviders();
        createdOrMergedSocialProviders += "," + principalUser.providerUser().getProvider();
        account.setCreatedOrMergedSocialProviders(createdOrMergedSocialProviders);
        account.setEmailVerified(true);

        accountService.saveAuthentication(request, response, account, account.getPassword(), true);
        JwtClaimDto jwtClaimDto = modelMapper.map(account, JwtClaimDto.class);
        return jwtClaimDto;
    }


}
