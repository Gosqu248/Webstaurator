package com.gosqu.auth.twoFactor;

import com.gosqu.auth.config.AuthProperties;
import com.gosqu.auth.exception.InvalidCodeException;
import com.gosqu.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoFactorService {

    private static final String TWO_FA_PREFIX  = "2fa:";
    private static final String RESET_PREFIX   = "reset:";
    private static final String OAUTH2_PREFIX  = "oauth2:code:";
    private static final Duration CODE_TTL     = Duration.ofMinutes(5);
    private static final Duration OAUTH2_TTL   = Duration.ofSeconds(60);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final StringRedisTemplate redis;
    private final EmailService emailService;
    private final AuthProperties authProperties;

    public void generateAndSendCode(String email) {
        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        redis.opsForValue().set(TWO_FA_PREFIX + email, code, CODE_TTL);
        emailService.send2FACode(email, code);
    }

    public void validateCode(String email, String code) {
        String stored = redis.opsForValue().get(TWO_FA_PREFIX + email);
        if (stored == null || !stored.equals(code)) {
            throw new InvalidCodeException();
        }
        redis.delete(TWO_FA_PREFIX + email);
    }

    public void generateResetToken(String email) {
        String token = UUID.randomUUID().toString();
        Duration resetTtl = Duration.ofHours(authProperties.passwordResetTokenTtlHours());
        redis.opsForValue().set(RESET_PREFIX + token, email, resetTtl);
        emailService.sendPasswordReset(email, token);
    }

    public String validateResetToken(String token) {
        String email = redis.opsForValue().get(RESET_PREFIX + token);
        if (email == null) {
            throw new InvalidCodeException();
        }
        redis.delete(RESET_PREFIX + token);
        return email;
    }

    public String storeOAuth2Code(String jwt) {
        String code = UUID.randomUUID().toString();
        redis.opsForValue().set(OAUTH2_PREFIX + code, jwt, OAUTH2_TTL);
        return code;
    }

    public String exchangeOAuth2Code(String code) {
        String jwt = redis.opsForValue().get(OAUTH2_PREFIX + code);
        if (jwt == null) {
            throw new InvalidCodeException();
        }
        redis.delete(OAUTH2_PREFIX + code);
        return jwt;
    }
}
