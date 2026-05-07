package org.example.auth_service.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;

/**
 * [JWT 생성 전용 클래스 - RSA 비공개 키로 서명합니다]
 * Auth_Service만 토큰을 생성하며, Api_Gateway는 공개 키만 갖습니다.
 */
@Slf4j
@Component
public class JwtUtils {

    private final PrivateKey privateKey;
    private final long accessTokenExpTime;

    public JwtUtils(
            @Value("${jwt.private-key}") String privateKeyPem,
            @Value("${jwt.expiration_time}") long accessTokenExpTime
    ) {
        this.privateKey = loadPrivateKey(privateKeyPem);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    /**
     * PEM 문자열에서 RSA PrivateKey 로드 (PKCS#8 형식)
     */
    private PrivateKey loadPrivateKey(String pem) {
        try {
            String stripped = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] keyBytes = Base64.getDecoder().decode(stripped);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA private key", e);
        }
    }

    /**
     * Access Token 생성 (RS256 서명)
     *
     * @param jwtClaimDto 클레임 정보
     * @return Access Token 문자열
     */
    public String createAccessToken(JwtClaimDto jwtClaimDto) {
        return createToken(jwtClaimDto, accessTokenExpTime);
    }

    /**
     * JWT 생성 (RS256)
     */
    private String createToken(JwtClaimDto jwtClaimDto, long expireTime) {
        Claims claims = Jwts.claims()
                .add("nickname", jwtClaimDto.getNickname())
                .add("email", jwtClaimDto.getEmail())
                .build();

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(tokenValidity.toInstant()))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
}