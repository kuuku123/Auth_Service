package org.example.auth_service.module.account.repository;

import org.example.auth_service.module.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AccountRepository extends JpaRepository<Account,Long>  {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String emailOrNickname);
}
