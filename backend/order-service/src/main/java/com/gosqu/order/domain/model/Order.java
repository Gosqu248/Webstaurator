package com.gosqu.order.domain.model;

import com.gosqu.order.domain.exception.ForbiddenOrderAccessException;
import com.gosqu.order.domain.exception.InvalidOrderTransitionException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final UUID customerId;
    private final UUID restaurantId;
    private final String restaurantName;
    private final List<OrderItem> items;
    private final DeliveryAddress deliveryAddress;
    private final Money deliveryFee;
    private final Money totalAmount;
    private OrderStatus status;
    private String cancellationReason;
    private final Instant createdAt;
    private Instant updatedAt;

    private Order(UUID id, UUID customerId, UUID restaurantId, String restaurantName,
                  List<OrderItem> items, DeliveryAddress deliveryAddress,
                  Money deliveryFee, Money totalAmount, OrderStatus status,
                  String cancellationReason, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.items = List.copyOf(items);
        this.deliveryAddress = deliveryAddress;
        this.deliveryFee = deliveryFee;
        this.totalAmount = totalAmount;
        this.status = status;
        this.cancellationReason = cancellationReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order create(UUID customerId, UUID restaurantId, String restaurantName,
                               List<OrderItem> items, DeliveryAddress deliveryAddress,
                               Money deliveryFee) {
        Money total = items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.PLN(BigDecimal.ZERO), Money::add)
                .add(deliveryFee);

        Instant now = Instant.now();
        return new Order(UUID.randomUUID(), customerId, restaurantId, restaurantName,
                items, deliveryAddress, deliveryFee, total,
                OrderStatus.PENDING, null, now, now);
    }

    public static Order reconstruct(UUID id, UUID customerId, UUID restaurantId, String restaurantName,
                                    List<OrderItem> items, DeliveryAddress deliveryAddress,
                                    Money deliveryFee, Money totalAmount, OrderStatus status,
                                    String cancellationReason, Instant createdAt, Instant updatedAt) {
        return new Order(id, customerId, restaurantId, restaurantName, items, deliveryAddress,
                deliveryFee, totalAmount, status, cancellationReason, createdAt, updatedAt);
    }

    public void confirm() {
        requireStatus(OrderStatus.PENDING, OrderStatus.CONFIRMED);
        status = OrderStatus.CONFIRMED;
        updatedAt = Instant.now();
    }

    public void startPreparing() {
        requireStatus(OrderStatus.CONFIRMED, OrderStatus.PREPARING);
        status = OrderStatus.PREPARING;
        updatedAt = Instant.now();
    }

    public void markPrepared() {
        requireStatus(OrderStatus.PREPARING, OrderStatus.PREPARED);
        status = OrderStatus.PREPARED;
        updatedAt = Instant.now();
    }

    public void markPickedUp() {
        requireStatus(OrderStatus.PREPARED, OrderStatus.PICKED_UP);
        status = OrderStatus.PICKED_UP;
        updatedAt = Instant.now();
    }

    public void markDelivered() {
        requireStatus(OrderStatus.PICKED_UP, OrderStatus.DELIVERED);
        status = OrderStatus.DELIVERED;
        updatedAt = Instant.now();
    }

    public void cancel(String reason) {
        if (status == OrderStatus.DELIVERED || status == OrderStatus.PICKED_UP) {
            throw new InvalidOrderTransitionException(id, status, OrderStatus.CANCELLED);
        }
        status = OrderStatus.CANCELLED;
        cancellationReason = reason;
        updatedAt = Instant.now();
    }

    public void failPayment() {
        requireStatus(OrderStatus.PENDING, OrderStatus.PAYMENT_FAILED);
        status = OrderStatus.PAYMENT_FAILED;
        updatedAt = Instant.now();
    }

    public void requireOwnership(UUID userId) {
        if (!customerId.equals(userId)) {
            throw new ForbiddenOrderAccessException(id, userId);
        }
    }

    private void requireStatus(OrderStatus expected, OrderStatus target) {
        if (status != expected) {
            throw new InvalidOrderTransitionException(id, status, target);
        }
    }

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public UUID getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public List<OrderItem> getItems() { return items; }
    public DeliveryAddress getDeliveryAddress() { return deliveryAddress; }
    public Money getDeliveryFee() { return deliveryFee; }
    public Money getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public String getCancellationReason() { return cancellationReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
