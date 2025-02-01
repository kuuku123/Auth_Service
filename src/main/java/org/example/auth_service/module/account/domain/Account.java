package org.example.auth_service.module.account.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(unique = true)
    private BigInteger subSocialIdentifier;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified; // check if current account is verified with email account

    private String emailCheckToken; // token used for email verification

    private LocalDateTime joinedAt; // specific time when email verification succeed and consider this user is signed in

    //client's extra info

    private String bio;

    private String url;

    private String occupation;

    private String location; // varchar(255) above all info

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    private boolean studyCreatedByEmail;

    @Builder.Default
    private boolean studyCreatedByWeb = true;

    private boolean studyEnrollmentResultByEmail;

    @Builder.Default
    private boolean studyEnrollmentResultByWeb = true;

    private boolean studyUpdatedByEmail;

    private String createdOrMergedSocialProviders = "";

    @Builder.Default
    private boolean studyUpdatedByWeb = true;
    private LocalDateTime emailCheckTokenGeneratedAt;

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
