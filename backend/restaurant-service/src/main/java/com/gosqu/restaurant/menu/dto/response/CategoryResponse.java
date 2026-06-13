package com.gosqu.restaurant.menu.dto.response;


import com.gosqu.restaurant.menu.Category;

import java.util.UUID;

public record CategoryResponse(UUID id, String name, Integer displayOrder) {
    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDisplayOrder());
    }
}
