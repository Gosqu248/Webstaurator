package com.gosqu.restaurant.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    List<Restaurant> findAllByOwnerId(UUID ownerId);

    @Query("""
            SELECT r FROM Restaurant r
            WHERE r.isActive = true
              AND (:city IS NULL OR LOWER(r.city) = LOWER(:city))
              AND (:cuisineType IS NULL OR r.cuisineType = :cuisineType)
              AND (:search IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Restaurant> search(@Param("city") String city,
                            @Param("cuisineType") CuisineType cuisineType,
                            @Param("search") String search,
                            Pageable pageable);

    @Modifying
    @Query("UPDATE Restaurant r SET r.avgRating = :rating WHERE r.id = :id")
    void updateAvgRating(@Param("id") UUID id, @Param("rating") Double rating);

    @Modifying
    @Query("UPDATE Restaurant r SET r.isActive = false WHERE r.id = :id")
    void deactivateById(@Param("id") UUID id);
}
