package com.gosqu.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilTest {

    private static KeyPair keyPair;
    private static KeyPair wrongKeyPair;

    private JwtUtil jwtUtil;

    @BeforeAll
    static void generateKeys() throws Exception {
        keyPair = generateRsaKeyPair();
        wrongKeyPair = generateRsaKeyPair();
    }

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil(new DefaultResourceLoader(), toPem(keyPair.getPublic()));
    }

    @Test
    @DisplayName("extractClaims return correct subject, role, userId")
    void extractClaims_validToken_returnCorrectClaims() {
        String token = buildToken(keyPair.getPrivate(), "gosqu@gosqu.com", "ADMIN", "777", 3_600_000);

        Claims claims = jwtUtil.extractClaims(token);

        assertEquals("gosqu@gosqu.com", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals("777", claims.get("userId", String.class));
    }

    @Test
    @DisplayName("isValid return true for fresh token")
    void isValid_freshToken_returnsTrue() {
        String token = buildToken(keyPair.getPrivate(), "gosqu@test.gosqu", "CUSTOMER", "1", 3_600_000);
        assertThat(jwtUtil.isValid(token)).isTrue();
    }

    @Test
    @DisplayName("isValid return false for expired token")
    void isValid_expiredToken_returnsFalse() {
        String token = buildToken(keyPair.getPrivate(), "gosqu@gosqu.com", "CUSTOMER", "1", -1_000);
        assertThat(jwtUtil.isValid(token)).isFalse();
    }

    @Test
    @DisplayName("isValid return false for token signed with wrong key")
    void isValid_wrongKey_returnsFalse() {
        String tampered = buildToken(wrongKeyPair.getPrivate(), "hacker@evil.com", "ADMIN", "1", 3_600_000);
        assertThat(jwtUtil.isValid(tampered)).isFalse();
    }

    @Test
    @DisplayName("extractClaims throw JwtException for incorrect string")
    void extractClaims_garbage_throwsJwtException() {
        assertThatThrownBy(() -> jwtUtil.extractClaims("not.a.real.token"))
                .isInstanceOf(JwtException.class);
    }

    // expiresInMs w nazwie, plusSeconds w implementacji — zachowane z oryginalnego testu, żeby
    // nie zmieniać efektywnych okien czasowych testu przy okazji migracji HMAC -> RSA.
    private static String buildToken(PrivateKey privateKey, String email, String role, String userId, long expiresInMs) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("userId", userId)
                .expiration(Date.from(Instant.now().plusSeconds(expiresInMs)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private static KeyPair generateRsaKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static String toPem(PublicKey key) {
        String base64 = Base64.getEncoder().encodeToString(key.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
    }
}
