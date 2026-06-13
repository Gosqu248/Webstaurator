package com.gosqu.restaurant.restaurant.dto.response;


import com.gosqu.restaurant.restaurant.Restaurant;

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
) {
    public static RestaurantResponse from(Restaurant r) {
        return new RestaurantResponse(
                r.getId(), r.getName(), r.getDescription(),
                r.getCuisineType().name(), r.getAddress(), r.getCity(),
                r.getLatitude(), r.getLongitude(), r.getPhoneNumber(),
                r.getLogoUrl(), r.getBannerUrl(), r.getIsActive(),
                r.getAvgRating(), r.getDeliveryTimeMin(),
                r.getDeliveryFee(), r.getMinOrderAmount()
        );
    }
}
