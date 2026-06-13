package com.gosqu.auth.service;

import com.gosqu.auth.user.Email;
import com.gosqu.auth.dto.request.ConfirmPasswordResetRequest;
import com.gosqu.auth.dto.request.LoginRequest;
import com.gosqu.auth.dto.request.RegisterRequest;
import com.gosqu.auth.dto.request.TwoFactorRequest;
import com.gosqu.auth.dto.response.AuthResponse;
import com.gosqu.auth.dto.response.UserResponse;
import com.gosqu.auth.exception.EmailAlreadyExistsException;
import com.gosqu.auth.exception.UserNotFoundException;
import com.gosqu.auth.jwt.JwtService;
import com.gosqu.auth.jwt.TokenBlackListService;
import com.gosqu.auth.refreshToken.RefreshTokenResult;
import com.gosqu.auth.refreshToken.RefreshTokenService;
import com.gosqu.auth.twoFactor.TwoFactorService;
import com.gosqu.auth.user.HashedPassword;
import com.gosqu.auth.user.User;
import com.gosqu.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TwoFactorService twoFactorService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlackListService tokenBlackListService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = User.builder()
                .email(Email.of(request.email()))
                .name(request.name())
                .password(HashedPassword.fromPlainText(request.password(), passwordEncoder))
                .build();
        userRepository.save(user);
        log.info("User registered: {}", request.email());
    }

    public void initiateLogin(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (user.getPassword() == null || !user.getPassword().matches(request.password(), passwordEncoder)) {
            log.warn("Failed login attempt for: {}", request.email());
            throw new BadCredentialsException("Invalid credentials");
        }

        twoFactorService.generateAndSendCode(user.getEmail().value());
        log.info("2FA code sent to: {}", request.email());
    }

    @Transactional
    public LoginResult verify2FA(TwoFactorRequest request) {
        twoFactorService.validateCode(request.email(), request.code());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        String accessToken = jwtService.generateToken(user.getEmail().value(), user.getRole(), user.getId());
        RefreshTokenResult refreshToken = refreshTokenService.createRefreshToken(user.getId());

        log.info("2FA verified, tokens issued for: {}", user.getEmail());

        return new LoginResult(
                accessToken,
                refreshToken.rawToken(),
                user.getEmail().value(),
                user.getName(),
                user.getRole().name()
        );
    }

    @Transactional
    public LoginResult refreshAccessToken(String rawRefreshToken) {
        RefreshTokenResult rotated = refreshTokenService.rotateRefreshToken(rawRefreshToken);
        User user = rotated.entity().getUser();
        String newAccessToken = jwtService.generateToken(user.getEmail().value(), user.getRole(), user.getId());

        log.info("Access token refreshed for: {}", user.getEmail());

        return new LoginResult(
                newAccessToken,
                rotated.rawToken(),
                user.getEmail().value(),
                user.getName(),
                user.getRole().name()
        );
    }

    @Transactional
    public void logout(String rawRefreshToken, String accessToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            try {
                String jti = jwtService.extractJti(accessToken);
                Date expiration = jwtService.extractExpiration(accessToken);
                Duration remaining = Duration.between(Instant.now(), expiration.toInstant());
                tokenBlackListService.blackList(jti, remaining);
            } catch (Exception e) {
                log.debug("Could not blacklist access token during logout: {}", e.getMessage());
            }
        }

        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            refreshTokenService.deleteByToken(rawRefreshToken);
        }

        log.info("User logged out from current session");
    }

    @Transactional
    public String handleOAuth2Login(OAuth2User oAuth2User) {
        String email    = oAuth2User.getAttribute("email");
        String name     = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");

        User user = userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(Email.of(email))
                        .name(name)
                        .googleId(googleId)
                        .build())
        );

        String jwt = jwtService.generateToken(user.getEmail().value(), user.getRole(), user.getId());
        return twoFactorService.storeOAuth2Code(jwt);
    }

    public AuthResponse exchangeOAuth2Code(String code) {
        String jwt = twoFactorService.exchangeOAuth2Code(code);
        String email = jwtService.extractSubject(jwt);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return new AuthResponse(jwt, user.getEmail().value(), user.getName(), user.getRole().name());
    }

    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email)
                .ifPresent(u -> twoFactorService.generateResetToken(u.getEmail().value()));
    }

    @Transactional
    public void confirmPasswordReset(ConfirmPasswordResetRequest request) {
        String email = twoFactorService.validateResetToken(request.token());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.setPassword(HashedPassword.fromPlainText(request.newPassword(), passwordEncoder));
        userRepository.save(user);
        refreshTokenService.deleteByUserId(user.getId());
        log.info("Password reset and all sessions invalidated for user: {}", user.getId());
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return new UserResponse(
                user.getId(),
                user.getEmail().value(),
                user.getName(),
                user.getRole().name()
        );
    }
}
