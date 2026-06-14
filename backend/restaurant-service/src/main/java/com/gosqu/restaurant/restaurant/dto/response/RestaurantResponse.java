package com.gosqu.restaurant.restaurant.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        String description,
        String cuisineType,
        String address,
        String city,
        Double latitude,
        Double longitude,
        String phoneNumber,
        String logoUrl,
        String bannerUrl,
        Boolean isActive,
        Double avgRating,
        Integer deliveryTimeMin,
        BigDecimal deliveryFee,
        BigDecimal minOrderAmount
) {}
