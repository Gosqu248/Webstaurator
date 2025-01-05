package pl.urban.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.UserDTO;
import pl.urban.backend.dto.UserInfoForOrderDTO;
import pl.urban.backend.model.User;
import pl.urban.backend.request.JwtResponse;
import pl.urban.backend.request.LoginRequest;
import pl.urban.backend.request.PasswordResetRequest;
import pl.urban.backend.request.TwoFactorVerificationRequest;
import pl.urban.backend.security.JwtUtil;
import pl.urban.backend.service.EmailService;
import pl.urban.backend.service.UserSecurityService;
import pl.urban.backend.service.UserService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtToken;
    private final AuthenticationManager authenticationManager;
    private final UserSecurityService userSecurityService;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    public AuthController(UserService userService, JwtUtil tokenProvider, AuthenticationManager authenticationManager, UserSecurityService userSecurityService, EmailService emailService) {
        this.userService = userService;
        this.jwtToken = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userSecurityService = userSecurityService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) throws Exception {
        logger.info("Login attempt for user: {}", loginRequest.getEmail());
        User user = userService.getUserBySubject(loginRequest.getEmail());

        if (userSecurityService.isAccountLocked(user)) {
            logger.warn("Account is locked for user: {}", loginRequest.getEmail());
            return ResponseEntity.status(423).body("Account is locked. Try again later.");
        }

        try {
            logger.info("Authenticating user: {}", loginRequest.getEmail());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            userSecurityService.resetFailedLoginAttempts(user);
            userSecurityService.generateAndSendTwoFactorCode(user);
        } catch (BadCredentialsException e) {
            logger.error("Bad credentials for user: {}", loginRequest.getEmail());
            userSecurityService.incrementFailedLoginAttempts(user);
            throw new Exception("Incorrect email or password", e);
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyTwoFactorCode(@RequestBody TwoFactorVerificationRequest request) throws Exception {
        User user = userService.getUserBySubject(request.getEmail());

        if(user == null) {
            throw new IllegalArgumentException("User with this email not found");
        }

        if (userSecurityService.verifyTwoFactorCode(user, request.getCode())) {
            final String jwt = jwtToken.generateToken(user);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } else {
            throw new Exception("Bad verification codes");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        User user = userService.getUserBySubject(request.getEmail());

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Użytkownik nie znaleziony"));
        }

        String token = jwtToken.generateToken(user);

        String resetUrl = "<a href='http://localhost/reset-password?token=" + token + "'>http://localhost/reset-password?token=" + token + "</a>";
        String emailContent = "Resetowanie hasła do strony Webstaurator. <br> <br>  Kliknij w poniższy link, aby zresetować hasło: <br><br>" + resetUrl;

        emailService.sendEmail(user.getEmail(), "Resetowanie hasła", emailContent);

        return ResponseEntity.ok(Map.of("message", "Link do resetowania hasła został wysłany na Twój email."));
    }
    @GetMapping("/user")
    public UserDTO getUser(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return userService.getUser(subject);
    }

    @GetMapping("/role")
    public ResponseEntity<String> getRole(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok().header("Content-Type", "text/plain").body(userService.getRole(subject));
    }

    @PutMapping("/changeName")
    public ResponseEntity<String>  changeUserName(@RequestHeader("Authorization") String token, @RequestBody String name) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        String updatedName = userService.changeName(subject, name);
        return ResponseEntity.ok(updatedName);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Boolean> changePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> passwords) {
        String subject =jwtToken.extractSubjectFromToken(token.substring(7));
        String password = passwords.get("password");
        String newPassword = passwords.get("newPassword");
        Boolean updatePassword = userService.changePassword(subject, password, newPassword);
        return ResponseEntity.ok(updatePassword);
    }

    @GetMapping("/userInfo")
    public ResponseEntity<UserInfoForOrderDTO> getUserInfo(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        UserInfoForOrderDTO userInfo = userService.getUserInfo(subject);
        return ResponseEntity.ok(userInfo);
    }

}
