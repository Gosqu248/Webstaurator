package com.gosqu.auth.controller;

import com.gosqu.auth.dto.request.*;
import com.gosqu.auth.dto.response.AuthResponse;
import com.gosqu.auth.dto.response.MessageResponse;
import com.gosqu.auth.dto.response.UserResponse;
import com.gosqu.auth.service.AuthService;
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
    public AuthResponse verify2FA(@RequestBody @Valid TwoFactorRequest request) {
        return authService.verify2FA(request);
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

    @GetMapping("/me")
    public UserResponse getCurrentUser(Principal principal) {
        return authService.getCurrentUser(principal.getName());
    }
}
