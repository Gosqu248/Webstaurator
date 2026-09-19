package com.gosqu.order.infrastructure.client.dto;

public record AddressSnapshot(
        String street,
        String city,
        String postalCode,
        String country,
        Double latitude,
        Double longitude) {
}
