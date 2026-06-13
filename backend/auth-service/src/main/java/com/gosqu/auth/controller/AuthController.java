package com.gosqu.auth.controller;

import com.gosqu.auth.dto.request.*;
import com.gosqu.auth.dto.response.AuthResponse;
import com.gosqu.auth.dto.response.MessageResponse;
import com.gosqu.auth.dto.response.UserResponse;
import com.gosqu.auth.refreshToken.exception.InvalidRefreshTokenException;
import com.gosqu.auth.service.AuthService;
import com.gosqu.auth.service.CookieService;
import com.gosqu.auth.service.LoginResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return new MessageResponse("Registration successful. Welcome to Webstaurator!");
    }

    @PostMapping("/login")
    public MessageResponse login(@RequestBody @Valid LoginRequest request) {
        authService.initiateLogin(request);
        return new MessageResponse("2FA code sent to your email.");
    }

    @PostMapping("/verify-2fa")
    public AuthResponse verify2FA(@RequestBody @Valid TwoFactorRequest request, HttpServletResponse response) {
        LoginResult result = authService.verify2FA(request);
        cookieService.addRefreshTokenCookie(response, result.rawRefreshToken());
        return new AuthResponse(result.accessToken(), result.email(), result.name(), result.role());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.extractRefreshToken(request)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token missing"));
        LoginResult result = authService.refreshAccessToken(refreshToken);
        cookieService.addRefreshTokenCookie(response, result.rawRefreshToken());
        return new AuthResponse(result.accessToken(), result.email(), result.name(), result.role());
    }

    @PostMapping("/logout")
    public MessageResponse logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.extractRefreshToken(request).orElse(null);
        String accessToken = extractBearerToken(request);
        authService.logout(refreshToken, accessToken);
        cookieService.clearRefreshTokenCookie(response);
        return new MessageResponse("Logged out successfully.");
    }

    @PostMapping("/reset-password/request")
    public MessageResponse requestPasswordReset(@RequestBody @Valid ResetPasswordRequest request) {
        authService.requestPasswordReset(request.email());
        return new MessageResponse("Password reset link sent to your email.");
    }

    @PostMapping("/reset-password/confirm")
    public MessageResponse confirmPasswordReset(@RequestBody @Valid ConfirmPasswordResetRequest request) {
        authService.confirmPasswordReset(request);
        return new MessageResponse("Password changed successfully.");
    }

    @PostMapping("/oauth2/exchange")
    public AuthResponse exchangeOAuth2Code(@RequestBody @Valid OAuth2ExchangeRequest request) {
        return authService.exchangeOAuth2Code(request.code());
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Principal principal) {
        return authService.getCurrentUser(principal.getName());
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
