package com.gosqu.restaurant.menu.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemRequest(
        @NotNull UUID categoryId,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 500) String description,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @Min(0) Integer preparationTimeMin,
        @Min(0) Integer calories
) {}
