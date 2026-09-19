package com.gosqu.order.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;

public record MenuClientResponse(
        List<CategoryWithItems> categories) {

    public record CategoryWithItems(
            UUID id,
            String name,
            List<MenuItemSnapshot> items) {
    }
}
