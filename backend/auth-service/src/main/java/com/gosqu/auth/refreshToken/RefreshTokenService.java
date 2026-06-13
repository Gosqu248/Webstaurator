package com.gosqu.auth.refreshToken;

import com.gosqu.auth.config.AuthProperties;
import com.gosqu.auth.refreshToken.exception.InvalidRefreshTokenException;
import com.gosqu.auth.user.User;
import com.gosqu.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthProperties authProperties;

    @Transactional
    public RefreshTokenResult createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidRefreshTokenException("User not found"));

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = sha256Hex(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
            .token(hashedToken)
            .user(user)
            .expiryDate(calculateExpiryDate())
            .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.debug("Created refresh token for user: {}", userId);

        return new RefreshTokenResult(saved, rawToken);
    }

    @Transactional
    public RefreshToken verifyRefreshToken(String rawToken) {
        String hashedToken = sha256Hex(rawToken);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashedToken)
            .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Transactional
    public RefreshTokenResult rotateRefreshToken(String oldRawToken) {
        RefreshToken existingToken = verifyRefreshToken(oldRawToken);
        User user = existingToken.getUser();

        refreshTokenRepository.delete(existingToken);

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = sha256Hex(rawToken);

        RefreshToken newToken = RefreshToken.builder()
            .token(hashedToken)
            .user(user)
            .expiryDate(calculateExpiryDate())
            .build();

        RefreshToken saved = refreshTokenRepository.save(newToken);
        log.debug("Rotated refresh token for user: {}", user.getId());

        return new RefreshTokenResult(saved, rawToken);
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.debug("Deleted all refresh tokens for user: {}", userId);
    }

    @Transactional
    public void deleteByToken(String rawToken) {
        String hashedToken = sha256Hex(rawToken);
        int deleted = refreshTokenRepository.deleteByToken(hashedToken);
        if (deleted > 0) {
            log.debug("Deleted refresh token");
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public int cleanupExpiredTokens() {
        int deleted = refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired refresh tokens", deleted);
        }
        return deleted;
    }

    private LocalDateTime calculateExpiryDate() {
        long ttlSeconds = authProperties.refreshTokenTtl() / 1000;
        return LocalDateTime.now().plusSeconds(ttlSeconds);
    }

    private static String sha256Hex(String input) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
