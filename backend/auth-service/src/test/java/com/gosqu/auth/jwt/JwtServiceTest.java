package com.gosqu.auth.jwt;

import com.gosqu.auth.config.AuthProperties;
import com.gosqu.auth.user.Email;
import com.gosqu.auth.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    static final KeyPair KEY_PAIR;

    static {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KEY_PAIR = gen.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private AuthProperties authProperties;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        authProperties = mock(AuthProperties.class);
        lenient().when(authProperties.accessTokenTtl()).thenReturn(3_600_000L);
        jwtService = new JwtService(KEY_PAIR.getPrivate(), KEY_PAIR.getPublic(), authProperties);
    }

    @Test
    void generateToken_returnsThreeSegmentJwt() {
        String token = jwtService.generateToken("test@test.com", Role.USER, UUID.randomUUID());

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractSubject_returnsCorrectEmail() {
        String token = jwtService.generateToken("gosqu@gosqu.com", Role.USER, UUID.randomUUID());

        assertThat(jwtService.extractSubject(token)).isEqualTo("gosqu@gosqu.com");
    }

    @Test
    void generateToken_containsRoleAndUserId() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("gosqu@gosqu.com", Role.USER, userId);

        Claims claims = Jwts.parser()
                        .verifyWith(KEY_PAIR.getPublic())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        assertThat(claims.get("userId", String.class)).isEqualTo(userId.toString());
        assertThat(claims.get("role", String.class)).isEqualTo(Role.USER.name());
    }

    @Test
    void isTokenValid_freshToken_returnsTrue() {
        Email email = Email.of("gosqu@gosqu.com");
        String token = jwtService.generateToken(email.value(), Role.USER, UUID.randomUUID());

        assertThat(jwtService.isTokenValid(token, email.value())).isTrue();
    }

    @Test
    void isTokenValid_wrongSubject_returnsFalse() {
        Email email = Email.of("gosqu@gosqu.com");
        String token = jwtService.generateToken(email.value(), Role.USER, UUID.randomUUID());

        assertThat(jwtService.isTokenValid(token, "wrong@email.com")).isFalse();
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() throws InterruptedException {
        AuthProperties shortProps = mock(AuthProperties.class);
        when(shortProps.accessTokenTtl()).thenReturn(1L);
        JwtService shortLived = new JwtService(KEY_PAIR.getPrivate(), KEY_PAIR.getPublic(), shortProps);

        String token = shortLived.generateToken("gosqu@gosqu.com", Role.USER, UUID.randomUUID());
        Thread.sleep(10);

        assertThat(jwtService.isTokenValid(token, "gosqu@gosqu.com")).isFalse();
    }

    @Test
    void isTokenValid_wrongKey_returnsFalse() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair wrongPair = gen.generateKeyPair();
        JwtService wrongKeyService = new JwtService(wrongPair.getPrivate(), wrongPair.getPublic(), authProperties);

        String tampered = wrongKeyService.generateToken("gosqu@gosqu.com", Role.USER, UUID.randomUUID());

        assertThat(jwtService.isTokenValid(tampered, "gosqu@gosqu.com")).isFalse();
    }

    @Test
    void isTokenValid_garbageToken_returnsFalse() {
        assertThat(jwtService.isTokenValid("not.a.valid.token", "gosqu@gosqu.com")).isFalse();
    }
}