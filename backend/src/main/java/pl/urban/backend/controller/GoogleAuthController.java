package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.urban.backend.dto.response.JwtResponse;
import pl.urban.backend.service.GoogleAuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class GoogleAuthController {
    private final GoogleAuthService googleAuthService;

    @GetMapping("/google")
    ResponseEntity<JwtResponse> googleLoginSuccess(@AuthenticationPrincipal OAuth2User principal) {
        try {
             return ResponseEntity.ok(new JwtResponse(googleAuthService.googleLogin(principal)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/google/failure")
    ResponseEntity<?> googleLoginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logowanie przez Google nie powiodło się.");
    }
}
