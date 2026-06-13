package com.gosqu.auth.service;

import com.gosqu.auth.dto.request.ConfirmPasswordResetRequest;
import com.gosqu.auth.dto.request.LoginRequest;
import com.gosqu.auth.dto.request.RegisterRequest;
import com.gosqu.auth.dto.request.TwoFactorRequest;
import com.gosqu.auth.dto.response.UserResponse;
import com.gosqu.auth.exception.EmailAlreadyExistsException;
import com.gosqu.auth.exception.InvalidCodeException;
import com.gosqu.auth.exception.UserNotFoundException;
import com.gosqu.auth.jwt.JwtService;
import com.gosqu.auth.jwt.TokenBlackListService;
import com.gosqu.auth.refreshToken.RefreshTokenResult;
import com.gosqu.auth.refreshToken.RefreshTokenService;
import com.gosqu.auth.twoFactor.TwoFactorService;
import com.gosqu.auth.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TwoFactorService twoFactorService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private TokenBlackListService tokenBlackListService;

    @InjectMocks
    private AuthService authService;

    private static final String BCRYPT_HASH = "$2a$10$testHashValueForUnit";
    private static final UUID TEST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");


    @Test
    void register_newEmail_savesUser() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn(BCRYPT_HASH);

        authService.register(new RegisterRequest("new@test.com", "Alice", "password123"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsEmailAlreadyExistsException() {
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("existing@test.com", "Alice", "password123")))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }


    @Test
    void initiateLogin_userNotFound_throwsBadCredentialsException() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.initiateLogin(
                new LoginRequest("ghost@test.com", "pass")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void initiateLogin_wrongPassword_throwsBadCredentialsException() {
        User user = buildUser("test@test.com", BCRYPT_HASH);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("wrong"), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.initiateLogin(
                new LoginRequest("test@test.com", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void initiateLogin_correctCredentials_sends2FACode() {
        User user = buildUser("test@test.com", BCRYPT_HASH);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("correct"), anyString())).thenReturn(true);

        authService.initiateLogin(new LoginRequest("test@test.com", "correct"));

        verify(twoFactorService).generateAndSendCode("test@test.com");
    }

    @Test
    void verify2FA_validCode_returnsLoginResultWithToken() {
        User user = buildUser("test@test.com", BCRYPT_HASH);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(eq("test@test.com"), eq(Role.USER), any(UUID.class)))
                .thenReturn("jwt.token.here");
        when(refreshTokenService.createRefreshToken(any(UUID.class)))
                .thenReturn(new RefreshTokenResult(null, "raw-refresh-token"));

        LoginResult result = authService.verify2FA(
                new TwoFactorRequest("test@test.com", "123456"));

        verify(twoFactorService).validateCode("test@test.com", "123456");
        assertThat(result.accessToken()).isEqualTo("jwt.token.here");
        assertThat(result.email()).isEqualTo("test@test.com");
        assertThat(result.rawRefreshToken()).isEqualTo("raw-refresh-token");
    }

    @Test
    void verify2FA_invalidCode_throwsInvalidCodeException() {
        doThrow(new InvalidCodeException())
                .when(twoFactorService).validateCode("test@test.com", "wrong");

        assertThatThrownBy(() -> authService.verify2FA(
                new TwoFactorRequest("test@test.com", "wrong")))
                .isInstanceOf(InvalidCodeException.class);
    }

    @Test
    void requestPasswordReset_existingEmail_generatesResetToken() {
        User user = buildUser("test@test.com", BCRYPT_HASH);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        authService.requestPasswordReset("test@test.com");

        verify(twoFactorService).generateResetToken("test@test.com");
    }

    @Test
    void requestPasswordReset_nonExistingEmail_doesNothing() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        authService.requestPasswordReset("ghost@test.com");

        verify(twoFactorService, never()).generateResetToken(any());
    }

    @Test
    void confirmPasswordReset_validToken_updatesPassword() {
        String newHash = "$2a$10$newHashValueForTest";
        User user = buildUser("test@test.com", BCRYPT_HASH);
        when(twoFactorService.validateResetToken("valid-token")).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn(newHash);

        authService.confirmPasswordReset(
                new ConfirmPasswordResetRequest("valid-token", "newPassword123"));

        assertThat(user.getPassword().value()).isEqualTo(newHash);
        verify(refreshTokenService).deleteByUserId(user.getId());
    }

    @Test
    void getCurrentUser_existingUser_returnsUserResponse() {
        User user = buildUser("test@test.com", BCRYPT_HASH);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        UserResponse response = authService.getCurrentUser("test@test.com");

        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.name()).isEqualTo("Test User");
    }

    @Test
    void getCurrentUser_nonExistingUser_throwsUserNotFoundException() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentUser("ghost@test.com"))
                .isInstanceOf(UserNotFoundException.class);
    }


    private User buildUser(String email, String bcryptHash) {
        return User.builder()
                .id(TEST_ID)
                .email(Email.of(email))
                .name("Test User")
                .password(HashedPassword.fromHash(bcryptHash))
                .role(Role.USER)
                .build();
    }
}