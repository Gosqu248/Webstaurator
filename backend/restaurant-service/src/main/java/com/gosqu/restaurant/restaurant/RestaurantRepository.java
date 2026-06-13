package com.gosqu.restaurant.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    Page<Restaurant> findAllByIsActiveTrue(Pageable pageable);

    Page<Restaurant> findAllByCityIgnoreCaseAndIsActiveTrue(String city, Pageable pageable);

    Page<Restaurant> findAllByCityIgnoreCaseAndCuisineTypeAndIsActiveTrue(
            String city, CuisineType cuisineType, Pageable pageable);

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
}
