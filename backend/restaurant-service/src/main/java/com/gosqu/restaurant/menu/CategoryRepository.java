package com.gosqu.restaurant.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByRestaurantIdAndIsActiveTrueOrderByDisplayOrder(UUID restaurantId);
    Optional<Category> findByIdAndRestaurantId(UUID id, UUID restaurantId);
}
