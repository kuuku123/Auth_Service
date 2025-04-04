package org.example.auth_service.security.config.auth_entity;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.security.config.auth_entity.dto.EmailVerificationDto;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailValidationService {

  private final StringRedisTemplate redisTemplate;


  public void storeValidationCode(String email, String code) {
    redisTemplate.opsForValue().set(email, code, 15, TimeUnit.MINUTES);
  }

  public boolean validateCode(EmailVerificationDto emailVerificationDto) {
    String email = emailVerificationDto.getEmail();
    String code = emailVerificationDto.getCode();
    String storedCode = redisTemplate.opsForValue().get(email);
    if (storedCode != null && storedCode.equals(code)) {
      redisTemplate.delete(email);
      return true;
    }
    return false;
  }
}