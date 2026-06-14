package com.gosqu.restaurant.menu.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        UUID categoryId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        Boolean isAvailable,
        Integer preparationTimeMin,
        Integer calories
) {}
