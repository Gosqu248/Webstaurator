package pl.urban.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.request.SignInWithTokenRequest;
import pl.urban.backend.dto.response.LoginResponse;
import pl.urban.backend.dto.response.UserInfoForOrderResponse;
import pl.urban.backend.dto.response.UserResponse;
import pl.urban.backend.model.User;
import pl.urban.backend.dto.response.JwtResponse;
import pl.urban.backend.dto.request.UserRequest;
import pl.urban.backend.dto.request.PasswordResetRequest;
import pl.urban.backend.dto.request.TwoFactorVerificationRequest;
import pl.urban.backend.config.security.JwtUtil;
import pl.urban.backend.service.EmailService;
import pl.urban.backend.service.UserSecurityService;
import pl.urban.backend.service.UserService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtToken;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserSecurityService userSecurityService;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRequest user) {
        userService.registerUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserRequest userRequest) {
        logger.info("Login attempt for user: {}", userRequest.email());

        try {
            User user = userService.getUserBySubject(userRequest.email());

            if (user == null) {
                logger.warn("User not found: {}", userRequest.email());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials");
            }

            if (userSecurityService.isAccountLocked(user)) {
                logger.warn("Account is locked for user: {}", userRequest.email());
                return ResponseEntity.status(423)
                        .body("Account is locked. Try again later.");
            }

            if (!passwordEncoder.matches(userRequest.password(), user.getPassword())) {
                logger.error("Invalid password for user: {}", userRequest.email());
                userSecurityService.incrementFailedLoginAttempts(user);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials");
            }

            userSecurityService.resetFailedLoginAttempts(user);
            userSecurityService.generateAndSendTwoFactorCode(user);

            return ResponseEntity.ok(true);

        } catch (Exception e) {
            logger.error("Error during login process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during login");
        }
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyTwoFactorCode(@RequestBody @Valid TwoFactorVerificationRequest request) throws Exception {
        User user = userService.getUserBySubject(request.email());

        if(user == null) {
            throw new IllegalArgumentException("User with this email not found");
        }

        if (userSecurityService.verifyTwoFactorCode(user, request.code())) {
            final String jwt = jwtToken.generateAuthToken(user);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } else {
            throw new Exception("Bad verification codes");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        User user = userService.getUserBySubject(request.email());

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Użytkownik nie znaleziony"));
        }

        String token = jwtToken.generatePasswordResetToken(user);

        String resetUrl = "<a href='http://localhost:4200/reset-password?token=" + token + "'>http://localhost:4200/reset-password?token=" + token + "</a>";
        String emailContent = "Resetowanie hasła do strony Webstaurator. <br> <br>  Kliknij w poniższy link, aby zresetować hasło: <br><br>" + resetUrl;

        emailService.sendEmail(user.getEmail(), "Resetowanie hasła", emailContent);

        return ResponseEntity.ok(Map.of("message", "Link do resetowania hasła został wysłany na Twój email."));
    }

    @GetMapping("sign-in-with-token")
    public ResponseEntity<LoginResponse> loginWithToken(
            @RequestBody @Valid SignInWithTokenRequest request
    ) {
        return ResponseEntity.ok(userService.signWithToken(request));
    }

    @GetMapping("/user")
    public UserResponse getUser(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubject(token.substring(7));
        return userService.getUser(subject);
    }

    @GetMapping("/role")
    public ResponseEntity<String> getRole(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubject(token.substring(7));
        return ResponseEntity.ok().header("Content-Type", "text/plain").body(userService.getRole(subject));
    }

    @PutMapping("/changeName")
    public ResponseEntity<String>  changeUserName(@RequestHeader("Authorization") String token, @RequestBody String name) {
        String subject = jwtToken.extractSubject(token.substring(7));
        String updatedName = userService.changeName(subject, name);
        return ResponseEntity.ok(updatedName);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Boolean> changePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> passwords) {
        String subject =jwtToken.extractSubject(token.substring(7));
        String password = passwords.get("password");
        String newPassword = passwords.get("newPassword");
        Boolean updatePassword = userService.changePassword(subject, password, newPassword);
        return ResponseEntity.ok(updatePassword);
    }

    @GetMapping("/userInfo")
    public ResponseEntity<UserInfoForOrderResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubject(token.substring(7));
        UserInfoForOrderResponse userInfo = userService.getUserInfo(subject);
        return ResponseEntity.ok(userInfo);
    }

}
