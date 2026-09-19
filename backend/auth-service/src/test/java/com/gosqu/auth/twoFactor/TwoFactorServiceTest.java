package com.gosqu.auth.twoFactor;

import com.gosqu.auth.config.AuthProperties;
import com.gosqu.auth.exception.InvalidCodeException;
import com.gosqu.auth.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwoFactorServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthProperties authProperties;

    @InjectMocks
    private TwoFactorService twoFactorService;

    @BeforeEach
    void setUp() {
        lenient().when(authProperties.passwordResetTokenTtlHours()).thenReturn(24);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void generateAndSendCode_storesCodeInRedisWithTtl() {
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);

        twoFactorService.generateAndSendCode("test@test.com");

        verify(valueOps).set(eq("2fa:test@test.com"), codeCaptor.capture(), eq(Duration.ofMinutes(5)));
        assertThat(codeCaptor.getValue()).matches("\\d{6}");
    }

    @Test
    void generateAndSendCode_sendsEmail() {
        twoFactorService.generateAndSendCode("test@test.com");

        verify(emailService).send2FACode(eq("test@test.com"), anyString());
    }

    @Test
    void validateCode_wrongCode_throwsInvalidCodeException() {
        when(valueOps.get("2fa:test@test.com")).thenReturn("123456");

        assertThatThrownBy(() -> twoFactorService.validateCode("test@test.com", "wrongCode"))
            .isInstanceOf(InvalidCodeException.class);
    }

    @Test
    void validateCode_noCodeInRedis_throwsInvalidCodeException() {
        when(valueOps.get("2fa:test@test.com")).thenReturn(null);

        assertThatThrownBy(() -> twoFactorService.validateCode("test@test.com", "123456"))
                .isInstanceOf(InvalidCodeException.class);
    }

    @Test
    void generateResetToken_storesTokenWithResetPrefix() {
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

        twoFactorService.generateResetToken("test@test.com");

        verify(valueOps).set(keyCaptor.capture(), eq("test@test.com"), eq(Duration.ofHours(24)));
        assertThat(keyCaptor.getValue()).startsWith("reset:");
    }

    @Test
    void generateResetToken_sendsEmailWithToken() {
        twoFactorService.generateResetToken("test@test.com");

        verify(emailService).sendPasswordReset(eq("test@test.com"), any());
    }

    @Test
    void validateResetToken_validToken_returnsEmailAndDeletesKey() {
        when(valueOps.get("reset:valid-token")).thenReturn("test@test.com");

        String email = twoFactorService.validateResetToken("valid-token");

        assertThat(email).isEqualTo("test@test.com");
        verify(redisTemplate).delete("reset:valid-token");
    }

    @Test
    void validateResetToken_invalidToken_throwsInvalidCodeException() {
        when(valueOps.get("reset:bad-token")).thenReturn(null);

        assertThatThrownBy(() -> twoFactorService.validateResetToken("bad-token"))
                .isInstanceOf(InvalidCodeException.class);
    }

    @Test
    void storeOAuth2Code_returnsCodeAndStoresJwt() {
        String code = twoFactorService.storeOAuth2Code("jwt.token.here");

        assertThat(code).isNotBlank();
        verify(valueOps).set(eq("oauth2:code:" + code), eq("jwt.token.here"), any(Duration.class));
    }

    @Test
    void exchangeOAuth2Code_validCode_returnsJwtAndDeletesKey() {
        when(valueOps.get("oauth2:code:my-code")).thenReturn("jwt.token.here");

        String jwt = twoFactorService.exchangeOAuth2Code("my-code");

        assertThat(jwt).isEqualTo("jwt.token.here");
        verify(redisTemplate).delete("oauth2:code:my-code");
    }

    @Test
    void exchangeOAuth2Code_invalidCode_throwsInvalidCodeException() {
        when(valueOps.get("oauth2:code:bad-code")).thenReturn(null);

        assertThatThrownBy(() -> twoFactorService.exchangeOAuth2Code("bad-code"))
                .isInstanceOf(InvalidCodeException.class);
    }
}