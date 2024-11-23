package pl.urban.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.CoordinatesDTO;
import pl.urban.backend.model.UserAddress;
import pl.urban.backend.security.JwtUtil;
import pl.urban.backend.service.UserAddressService;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class UserAddressController {


    private final UserAddressService userAddressService;
    private final JwtUtil jwtToken;

    public UserAddressController(UserAddressService userAddressService, JwtUtil jwtToken) {
        this.userAddressService = userAddressService;
        this.jwtToken = jwtToken;
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserAddress>> getAllUserAddresses(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        List<UserAddress> addresses = userAddressService.getAllAddresses(subject);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/add")
    public ResponseEntity<UserAddress> addAddressToUser(@RequestHeader("Authorization") String token, @RequestBody UserAddress userAddress) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok(userAddressService.addAddress(subject, userAddress));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        userAddressService.deleteAddress(subject, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAddress> updateAddress(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody UserAddress userAddress) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok(userAddressService.updateAddress(subject, id, userAddress));
    }

    @GetMapping("/available")
    public ResponseEntity<List<UserAddress>> getAvailableAddresses(
            @RequestHeader("Authorization") String token,
            @RequestParam CoordinatesDTO coordinates,
            @RequestParam(defaultValue = "6") double radius) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));

        return ResponseEntity.ok(userAddressService.findAvailableAddresses(subject, coordinates, radius));
    }

}
