package com.gosqu.order.application.port.out;

import com.gosqu.order.infrastructure.client.dto.MenuItemSnapshot;
import com.gosqu.order.infrastructure.client.dto.RestaurantSnapshot;

import java.util.List;
import java.util.UUID;

public interface RestaurantClientPort {

    RestaurantSnapshot getRestaurant(UUID restaurantId);

    List<MenuItemSnapshot> getMenuItems(UUID restaurantId, List<UUID> itemIds);
}
