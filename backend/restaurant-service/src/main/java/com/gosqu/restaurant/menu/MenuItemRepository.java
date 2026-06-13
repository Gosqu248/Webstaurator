package com.gosqu.restaurant.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findAllByCategoryIdAndIsAvailableTrue(UUID categoryId);
    List<MenuItem> findAllByRestaurantIdAndIsAvailableTrue(UUID restaurantId);
    Optional<MenuItem> findByIdAndRestaurantId(UUID id, UUID restaurantId);

    List<MenuItem> findAllByRestaurantIdAndIsAvailableTrueOrderByCategoryId(UUID restaurantId);
}
