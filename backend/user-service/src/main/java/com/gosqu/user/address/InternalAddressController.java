package com.gosqu.user.address;

import com.gosqu.user.address.dto.response.AddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
class InternalAddressController {

    private final AddressService addressService;

    @GetMapping("/{userId}/addresses/default")
    public AddressResponse getDefaultAddress(@PathVariable UUID userId) {
        return addressService.getDefaultAddress(userId);
    }
}
