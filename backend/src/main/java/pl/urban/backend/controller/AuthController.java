package pl.urban.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.urban.backend.model.User;
import pl.urban.backend.request.JwtResponse;
import pl.urban.backend.request.LoginRequest;
import pl.urban.backend.security.JwtUtil;
import pl.urban.backend.service.UserService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtil jwtToken;



    public AuthController(UserService userService, JwtUtil tokenProvider) {
        this.userService = userService;
        this.jwtToken = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
       if (userService.checkUserCredentials(loginRequest.getEmail(), loginRequest.getPassword())) {
           String token = jwtToken.createToken(loginRequest.getEmail());
           return ResponseEntity.ok(new JwtResponse(token));
       }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
    }

}
