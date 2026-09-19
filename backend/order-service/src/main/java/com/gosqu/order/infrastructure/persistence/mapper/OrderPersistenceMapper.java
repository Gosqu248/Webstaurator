package com.gosqu.order.infrastructure.persistence.mapper;

import com.gosqu.order.domain.model.*;
import com.gosqu.order.infrastructure.persistence.entity.OrderItemJpaEntity;
import com.gosqu.order.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderPersistenceMapper {

    public Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toItemDomain)
                .toList();

        DeliveryAddress address = new DeliveryAddress(
                entity.getDeliveryStreet(), entity.getDeliveryCity(),
                entity.getDeliveryPostalCode(), entity.getDeliveryCountry(),
                entity.getDeliveryLatitude(), entity.getDeliveryLongitude());

        Money deliveryFee = Money.of(entity.getDeliveryFeeAmount(), entity.getDeliveryFeeCurrency());
        Money total = Money.of(entity.getTotalAmount(), entity.getTotalCurrency());

        return Order.reconstruct(
                entity.getId(), entity.getCustomerId(), entity.getRestaurantId(),
                entity.getRestaurantName(), items, address, deliveryFee, total,
                entity.getStatus(), entity.getCancellationReason(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    public OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.getId());
        entity.setCustomerId(order.getCustomerId());
        entity.setRestaurantId(order.getRestaurantId());
        entity.setRestaurantName(order.getRestaurantName());
        entity.setDeliveryStreet(order.getDeliveryAddress().street());
        entity.setDeliveryCity(order.getDeliveryAddress().city());
        entity.setDeliveryPostalCode(order.getDeliveryAddress().postalCode());
        entity.setDeliveryCountry(order.getDeliveryAddress().country());
        entity.setDeliveryLatitude(order.getDeliveryAddress().latitude());
        entity.setDeliveryLongitude(order.getDeliveryAddress().longitude());
        entity.setDeliveryFeeAmount(order.getDeliveryFee().amount());
        entity.setDeliveryFeeCurrency(order.getDeliveryFee().currency());
        entity.setTotalAmount(order.getTotalAmount().amount());
        entity.setTotalCurrency(order.getTotalAmount().currency());
        entity.setStatus(order.getStatus());
        entity.setCancellationReason(order.getCancellationReason());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemJpaEntity> itemEntities = order.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .toList();
        entity.getItems().clear();
        entity.getItems().addAll(itemEntities);

        return entity;
    }

    private OrderItem toItemDomain(OrderItemJpaEntity entity) {
        return new OrderItem(entity.getId(), entity.getMenuItemId(), entity.getName(),
                Money.of(entity.getUnitPriceAmount(), entity.getUnitPriceCurrency()),
                entity.getQuantity());
    }

    private OrderItemJpaEntity toItemEntity(OrderItem item, OrderJpaEntity orderEntity) {
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.setId(item.id());
        entity.setOrder(orderEntity);
        entity.setMenuItemId(item.menuItemId());
        entity.setName(item.name());
        entity.setUnitPriceAmount(item.unitPrice().amount());
        entity.setUnitPriceCurrency(item.unitPrice().currency());
        entity.setQuantity(item.quantity());
        return entity;
    }
}
