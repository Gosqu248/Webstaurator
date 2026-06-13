package com.gosqu.restaurant.menu.dto.response;


import com.gosqu.restaurant.menu.MenuItem;

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
) {
    public static MenuItemResponse from(MenuItem m) {
        return new MenuItemResponse(
                m.getId(), m.getCategoryId(), m.getName(), m.getDescription(),
                m.getPrice(), m.getImageUrl(), m.getIsAvailable(),
                m.getPreparationTimeMin(), m.getCalories()
        );
    }
}
