package com.gosqu.user.address.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @Size(max = 30) String label,
        @NotBlank @Size(max = 200) String street,
        @NotBlank @Size(max = 100) String city,
        @Size(max = 10) String postalCode,
        @Size(max = 60) String country,
        Double latitude,
        Double longitude
) {}
