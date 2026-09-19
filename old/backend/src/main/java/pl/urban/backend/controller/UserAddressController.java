package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.request.UserAddressRequest;
import pl.urban.backend.dto.response.UserAddressResponse;
import pl.urban.backend.config.security.JwtUtil;
import pl.urban.backend.service.UserAddressService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/addresses")
public class UserAddressController {
    private final UserAddressService userAddressService;
    private final JwtUtil jwtToken;

    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> getAllUserAddresses(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubject(token.substring(7));
        return ResponseEntity.ok(userAddressService.getAllAddresses(subject));
    }

    @PostMapping
    public ResponseEntity<UserAddressResponse> addAddressToUser(@RequestHeader("Authorization") String token, @RequestBody UserAddressRequest addressRequest) {
        String subject = jwtToken.extractSubject(token.substring(7));
        return ResponseEntity.ok(userAddressService.addAddress(subject, addressRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String subject = jwtToken.extractSubject(token.substring(7));
        userAddressService.deleteAddress(subject, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAddressResponse> updateAddress(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody UserAddressRequest userAddress) {
        String subject = jwtToken.extractSubject(token.substring(7));
        return ResponseEntity.ok(userAddressService.updateAddress(subject, id, userAddress));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAddressResponse> getAddressById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String subject = jwtToken.extractSubject(token.substring(7));
        return ResponseEntity.ok(userAddressService.findAddressById(subject, id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<UserAddressResponse>> getAvailableAddresses(
            @RequestHeader("Authorization") String token,
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "6") double radius) {
        String subject = jwtToken.extractSubject(token.substring(7));

        return ResponseEntity.ok(userAddressService.findAvailableAddresses(subject, lat, lon, radius));
    }

}
