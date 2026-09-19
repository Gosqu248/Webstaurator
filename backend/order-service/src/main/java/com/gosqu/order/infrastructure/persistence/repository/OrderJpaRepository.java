package com.gosqu.order.infrastructure.persistence.repository;

import com.gosqu.order.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {

    List<OrderJpaEntity> findAllByCustomerId(UUID customerId);

    List<OrderJpaEntity> findAllByRestaurantId(UUID restaurantId);
}
