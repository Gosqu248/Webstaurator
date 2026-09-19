package com.gosqu.restaurant.hours;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OpeningHoursRepository extends JpaRepository<OpeningHours, UUID> {
    List<OpeningHours> findAllByRestaurantId(UUID restaurantId);
    void deleteAllByRestaurantId(UUID restaurantId);
}
