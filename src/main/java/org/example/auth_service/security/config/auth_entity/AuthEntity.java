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
  private String nickname;
  private String email;
  private String password;

  @Column(unique = true)
  private BigInteger subSocialIdentifier;

  private boolean emailVerified; // check if current account is verified with email account

  private String emailCheckToken; // token used for email verification

  private String createdOrMergedSocialProviders = "";

  private LocalDateTime emailCheckTokenGeneratedAt;

  private LocalDateTime joinedAt; // specific time when email verification succeed and consider this user is signed in

  public void generateEmailCheckToken() {
    this.emailCheckToken = UUID.randomUUID().toString();
    this.emailCheckTokenGeneratedAt = LocalDateTime.now();
  }

  public void completeSignUp() {
    this.emailVerified = true;
    this.joinedAt = LocalDateTime.now();
  }

  public boolean isValidToken(String token) {
    return this.emailCheckToken.equals(token);
  }

  public boolean canSendConfirmationEmail() {
//        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    return true;
  }

}
