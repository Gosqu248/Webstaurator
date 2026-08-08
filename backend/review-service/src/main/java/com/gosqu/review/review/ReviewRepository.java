package com.gosqu.review.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends MongoRepository<Review, String> {

    Page<Review> findByRestaurantIdAndVisibleTrue(UUID restaurantId, Pageable pageable);

    List<Review> findByCustomerId(UUID customerId);
}
