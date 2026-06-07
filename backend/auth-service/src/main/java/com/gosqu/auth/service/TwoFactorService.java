package com.gosqu.auth.service;

import com.gosqu.auth.exception.InvalidCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoFactorService {

    private static final String TWO_FA_PREFIX = "2fa:";
    private static final String RESET_PREFIX  = "reset:";
    private static final Duration CODE_TTL    = Duration.ofMinutes(5);
    private static final Duration RESET_TTL   = Duration.ofMinutes(15);

    private final StringRedisTemplate redis;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public void generateAndSendCode(String email) {
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
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
        redis.opsForValue().set(RESET_PREFIX + token, email, RESET_TTL);
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
}
