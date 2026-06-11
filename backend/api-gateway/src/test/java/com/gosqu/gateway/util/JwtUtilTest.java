package com.gosqu.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilTest {

    private static final String TEST_SECRET = Base64.getEncoder().encodeToString(
            "webstaurator-test-secret-key-must-be-at-least-256-bits-long!!".getBytes());

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET);
    }

    @Test
    @DisplayName("extractClaims return correct subject, role, userId")
    void extractClaims_validToken_returnCorrectClaims() {
        String token = buildToken("gosqu@gosqu.com", "ADMIN", 777L, 3_600_000);

        Claims claims = jwtUtil.extractClaims(token);

        assertEquals("gosqu@gosqu.com", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals(777L, claims.get("userId", Long.class));
    }

    @Test
    @DisplayName("isValid return true for fresh token")
    void isValid_freshToken_returnsTrue() {
        String token = buildToken("gosqu@test.gosqu", "CUSTOMER", 1L, 3_600_000);
        assertThat(jwtUtil.isValid(token)).isTrue();
    }

    @Test
    @DisplayName("isValid return false for expired token")
    void isValid_expiredToken_returnsFalse() {
        String token = buildToken("gosqu@gosqu.com", "CUSTOMER", 1L, -1_000);
        assertThat(jwtUtil.isValid(token)).isFalse();
    }

    @Test
    @DisplayName("isValid return false for token signed with wrong secret")
    void isValid_wrongSecret_returnsFalse() {
        String wrongSecret = Base64.getEncoder().encodeToString(
                "completely-different-secret-key-at-least-256-bits-long!!".getBytes());
        SecretKey wrongKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(wrongSecret));
        String tampered = Jwts.builder()
                .subject("hacker@evil.com")
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(wrongKey)
                .compact();

        assertThat(jwtUtil.isValid(tampered)).isFalse();
    }

    @Test
    @DisplayName("extractClaims throw JwtException for incorrect string")
    void extractClaims_garbage_throwsJwtException() {
        assertThatThrownBy(() -> jwtUtil.extractClaims("not.a.real.token"))
                .isInstanceOf(JwtException.class);
    }

    private String buildToken(String email, String role, long userId, long expiresInMs) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("userId", userId)
                .expiration(Date.from(Instant.now().plusSeconds(expiresInMs)))
                .signWith(key)
                .compact();
    }
}