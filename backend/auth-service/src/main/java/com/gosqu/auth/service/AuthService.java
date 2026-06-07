package com.gosqu.auth.service;

import com.gosqu.auth.dto.request.ConfirmPasswordResetRequest;
import com.gosqu.auth.dto.request.LoginRequest;
import com.gosqu.auth.dto.request.RegisterRequest;
import com.gosqu.auth.dto.request.TwoFactorRequest;
import com.gosqu.auth.dto.response.AuthResponse;
import com.gosqu.auth.dto.response.UserResponse;
import com.gosqu.auth.exception.EmailAlreadyExistsException;
import com.gosqu.auth.exception.UserNotFoundException;
import com.gosqu.auth.model.Role;
import com.gosqu.auth.model.User;
import com.gosqu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TwoFactorService twoFactorService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = User.builder()
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);
    }

    public void initiateLogin(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (user.getPassword() == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        twoFactorService.generateAndSendCode(user.getEmail());
    }

    public AuthResponse verify2FA(TwoFactorRequest request) {
        twoFactorService.validateCode(request.email(), request.code());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        return new AuthResponse(
                jwtService.generateToken(user.getEmail(), user.getRole()),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }

    @Transactional
    public String handleOAuth2Login(OAuth2User oAuth2User) {
        String email    = oAuth2User.getAttribute("email");
        String name     = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");

        User user = userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(email)
                        .name(name)
                        .googleId(googleId)
                        .role(Role.CUSTOMER)
                        .build())
        );

        return jwtService.generateToken(user.getEmail(), user.getRole());
    }

    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        twoFactorService.generateResetToken(email);
    }

    @Transactional
    public void confirmPasswordReset(ConfirmPasswordResetRequest request) {
        String email = twoFactorService.validateResetToken(request.token());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole().name());
    }
}
