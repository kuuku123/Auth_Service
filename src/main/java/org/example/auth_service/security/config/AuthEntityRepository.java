package org.example.auth_service.security.config;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthEntityRepository extends JpaRepository<AuthEntity, Long> {

    AuthEntity findByEmail(String email);
}
