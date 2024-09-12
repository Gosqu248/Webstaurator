package pl.urban.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.UserAddress;
import pl.urban.backend.security.JwtUtil;
import pl.urban.backend.service.UserAddressService;

@RestController
@RequestMapping("/api/address")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private JwtUtil jwtToken;

    @PostMapping("/add")
    public ResponseEntity<UserAddress> addAddressToUser(@RequestHeader("Authorization") String token, @RequestBody UserAddress userAddress) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok(userAddressService.addAddress(subject, userAddress));
    }
}
