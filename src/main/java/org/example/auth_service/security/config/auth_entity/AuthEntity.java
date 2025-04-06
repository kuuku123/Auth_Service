package org.example.auth_service.security.config.auth_entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Data
public class AuthEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String nickname;
  @Column(unique = true)
  private String email;
  private String password;

  @Column(unique = true)
  private BigInteger subSocialIdentifier;

  private String createdOrMergedSocialProviders = "";

  private LocalDateTime joinedAt; // specific time when email verification succeed and consider this user is signed in
}
