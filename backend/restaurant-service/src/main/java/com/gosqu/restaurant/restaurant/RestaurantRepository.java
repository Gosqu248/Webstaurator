package com.gosqu.restaurant.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    List<Restaurant> findAllByOwnerId(UUID ownerId);

    @Modifying
    @Query("UPDATE Restaurant r SET r.avgRating = :rating WHERE r.id = :id")
    void updateAvgRating(@Param("id") UUID id, @Param("rating") Double rating);

    @Modifying
    @Query("UPDATE Restaurant r SET r.isActive = false WHERE r.id = :id")
    void deactivateById(@Param("id") UUID id);
}
