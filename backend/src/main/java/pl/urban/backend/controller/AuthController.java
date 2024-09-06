package pl.urban.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.User;
import pl.urban.backend.request.JwtResponse;
import pl.urban.backend.request.LoginRequest;
import pl.urban.backend.security.JwtUtil;
import pl.urban.backend.service.DetailsUserService;
import pl.urban.backend.service.UserService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtToken;
    private final AuthenticationManager authenticationManager;
    private final DetailsUserService detailsUserService;



    public AuthController(UserService userService, JwtUtil tokenProvider, AuthenticationManager authenticationManager, DetailsUserService detailsUserService) {
        this.userService = userService;
        this.jwtToken = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.detailsUserService = detailsUserService;
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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect email or password", e);
        }

        final UserDetails userDetails = detailsUserService.loadUserByUsername(loginRequest.getEmail());

        final String jwt = jwtToken.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @GetMapping("/user")
    public User getUser(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return userService.getCustomerBySubject(subject);
    }

}
