package org.example.auth_service.security.config.auth_entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthEntityRepository extends JpaRepository<AuthEntity, Long> {

    AuthEntity findByEmail(String email);

    AuthEntity findByNickname(String nicknameOrEmail);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
