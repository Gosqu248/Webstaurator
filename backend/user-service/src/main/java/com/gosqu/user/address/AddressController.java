package com.gosqu.user.address;

import com.gosqu.user.address.dto.request.AddressRequest;
import com.gosqu.user.address.dto.response.AddressResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/me/addresses")
@RequiredArgsConstructor
@Validated
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public List<AddressResponse> getAddresses(@RequestHeader("X-User-Id") UUID userId) {
        return addressService.getAddresses(userId);
    }

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(@RequestHeader("X-User-Id") UUID userId,
                                                      @RequestBody @Valid AddressRequest request) {
        AddressResponse response = addressService.addAddress(userId, request);
        URI location = URI.create("/users/me/addresses/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public AddressResponse updateAddress(@RequestHeader("X-User-Id") UUID userId,
                                         @PathVariable UUID id,
                                         @RequestBody @Valid AddressRequest request) {
        return addressService.updateAddress(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteAddress(@RequestHeader("X-User-Id") UUID userId,
                              @PathVariable UUID id) {
        addressService.deleteAddress(userId, id);
    }

    @PutMapping("/{id}/default")
    public AddressResponse setDefault(@RequestHeader("X-User-Id") UUID userId,
                                      @PathVariable UUID id) {
        return addressService.setDefaultAddress(userId, id);
    }
}
