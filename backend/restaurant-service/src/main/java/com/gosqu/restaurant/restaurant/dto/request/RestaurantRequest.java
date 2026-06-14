package com.gosqu.restaurant.restaurant.dto.request;

import com.gosqu.restaurant.restaurant.CuisineType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RestaurantRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @NotNull CuisineType cuisineType,
        @NotBlank @Size(max = 200) String address,
        @NotBlank @Size(max = 100) String city,
        Double latitude,
        Double longitude,
        @Pattern(regexp = "^[+]?[0-9 \\-()]{7,20}$") String phoneNumber,
        @Min(0) Integer deliveryTimeMin,
        @DecimalMin("0.0") BigDecimal deliveryFee,
        @DecimalMin("0.0") BigDecimal minOrderAmount
) {}
