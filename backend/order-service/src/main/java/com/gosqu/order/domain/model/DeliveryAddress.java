package com.gosqu.order.domain.model;

public record DeliveryAddress(
        String street,
        String city,
        String postalCode,
        String country,
        Double latitude,
        Double longitude) {
}
