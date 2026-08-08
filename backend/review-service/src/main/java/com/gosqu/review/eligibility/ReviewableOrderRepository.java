package com.gosqu.review.eligibility;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewableOrderRepository extends MongoRepository<ReviewableOrder, String> {

    Optional<ReviewableOrder> findByOrderId(UUID orderId);
}
