package com.gosqu.order.infrastructure.persistence;

import com.gosqu.order.application.port.out.OrderRepositoryPort;
import com.gosqu.order.domain.model.Order;
import com.gosqu.order.infrastructure.persistence.mapper.OrderPersistenceMapper;
import com.gosqu.order.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;
    private final OrderPersistenceMapper mapper;

    @Override
    public Order save(Order order) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findById(orderId).map(mapper::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return jpaRepository.findAllByCustomerId(customerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByRestaurantId(UUID restaurantId) {
        return jpaRepository.findAllByRestaurantId(restaurantId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
