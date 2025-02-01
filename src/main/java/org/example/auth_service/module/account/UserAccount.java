package org.example.auth_service.module.account;

import lombok.Getter;
import org.example.auth_service.module.account.domain.Account;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {

    private Account account;

    public UserAccount(Account account) {
        super(account.getNickname(),account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
