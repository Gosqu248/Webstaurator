package com.gosqu.restaurant.menu.dto.response;

import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;

import java.util.List;
import java.util.UUID;

public record MenuResponse(
        RestaurantResponse restaurant,
        List<CategoryWithItemsResponse> categories
) {
    public record CategoryWithItemsResponse(
            UUID id,
            String name,
            Integer displayOrder,
            List<MenuItemResponse> items
    ) {}
}
