package com.gosqu.user.address.dto.response;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String label,
        String street,
        String city,
        String postalCode,
        String country,
        Double latitude,
        Double longitude,
        Boolean isDefault
) {}
