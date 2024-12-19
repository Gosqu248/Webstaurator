package pl.urban.backend.controller;

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
import pl.urban.backend.request.TwoFactorVerificationRequest;
import pl.urban.backend.security.JwtUtil;
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



    public AuthController(UserService userService, JwtUtil tokenProvider, AuthenticationManager authenticationManager, UserSecurityService userSecurityService) {
        this.userService = userService;
        this.jwtToken = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userSecurityService = userSecurityService;
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
        User user = userService.getUserBySubject(loginRequest.getEmail());

        if (userSecurityService.isAccountLocked(user)) {
            return  ResponseEntity.status(423).body("Account is locked. Try again later.");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            userSecurityService.resetFailedLoginAttempts(user);
            userSecurityService.generateAndSendTwoFactorCode(user);
        } catch (BadCredentialsException e) {
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
