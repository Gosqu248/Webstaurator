package com.gosqu.order.application.service;

import com.gosqu.order.application.dto.request.CreateOrderRequest;
import com.gosqu.order.application.dto.response.OrderItemResponse;
import com.gosqu.order.application.dto.response.OrderResponse;
import com.gosqu.order.application.port.in.CreateOrderUseCase;
import com.gosqu.order.application.port.in.GetOrderUseCase;
import com.gosqu.order.application.port.in.UpdateOrderStatusUseCase;
import com.gosqu.order.application.port.out.OrderEventPublisherPort;
import com.gosqu.order.application.port.out.OrderRepositoryPort;
import com.gosqu.order.application.port.out.RestaurantClientPort;
import com.gosqu.order.application.port.out.UserAddressClientPort;
import com.gosqu.order.domain.exception.OrderNotFoundException;
import com.gosqu.order.domain.model.*;
import com.gosqu.order.infrastructure.client.dto.AddressSnapshot;
import com.gosqu.order.infrastructure.client.dto.MenuItemSnapshot;
import com.gosqu.order.infrastructure.client.dto.RestaurantSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase, UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;
    private final RestaurantClientPort restaurantClient;
    private final UserAddressClientPort addressClient;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderCommand command) {
        log.info("action=create_order customerId={} restaurantId={}", command.customerId(), command.restaurantId());

        RestaurantSnapshot restaurant = restaurantClient.getRestaurant(command.restaurantId());
        if (Boolean.FALSE.equals(restaurant.isActive())) {
            throw new IllegalArgumentException("Restaurant is not active: " + command.restaurantId());
        }

        List<UUID> itemIds = command.items().stream().map(OrderItemCommand::menuItemId).toList();
        List<MenuItemSnapshot> menuItems = restaurantClient.getMenuItems(command.restaurantId(), itemIds);
        Map<UUID, MenuItemSnapshot> itemMap = menuItems.stream()
                .collect(Collectors.toMap(MenuItemSnapshot::id, Function.identity()));

        AddressSnapshot address = addressClient.getDefaultAddress(command.customerId());

        List<OrderItem> orderItems = command.items().stream()
                .map(req -> {
                    MenuItemSnapshot item = itemMap.get(req.menuItemId());
                    if (item == null) {
                        throw new IllegalArgumentException("Menu item not found: " + req.menuItemId());
                    }
                    if (Boolean.FALSE.equals(item.isAvailable())) {
                        throw new IllegalArgumentException("Menu item is not available: " + req.menuItemId());
                    }
                    return new OrderItem(UUID.randomUUID(), item.id(), item.name(),
                            Money.PLN(item.price()), req.quantity());
                })
                .toList();

        DeliveryAddress deliveryAddress = new DeliveryAddress(
                address.street(), address.city(), address.postalCode(),
                address.country(), address.latitude(), address.longitude());

        Money deliveryFee = Money.PLN(restaurant.deliveryFee());

        Order order = Order.create(command.customerId(), restaurant.id(), restaurant.name(),
                orderItems, deliveryAddress, deliveryFee);

        Order saved = orderRepository.save(order);
        eventPublisher.publishOrderCreated(saved);

        log.info("action=order_created orderId={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId, UUID requestingUserId) {
        Order order = findOrder(orderId);
        order.requireOwnership(requestingUserId);
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForCustomer(UUID customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForRestaurant(UUID restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void confirmOrder(UUID orderId) {
        Order order = findOrder(orderId);
        if (order.getStatus() == com.gosqu.order.domain.model.OrderStatus.CONFIRMED) {
            log.info("action=confirm_order_skipped orderId={} reason=already_confirmed", orderId);
            return;
        }
        order.confirm();
        orderRepository.save(order);
        eventPublisher.publishOrderConfirmed(order);
        log.info("action=order_confirmed orderId={}", orderId);
    }

    @Override
    @Transactional
    public void startPreparing(UUID orderId) {
        Order order = findOrder(orderId);
        order.startPreparing();
        orderRepository.save(order);
        log.info("action=order_preparing orderId={}", orderId);
    }

    @Override
    @Transactional
    public void markPrepared(UUID orderId) {
        Order order = findOrder(orderId);
        order.markPrepared();
        orderRepository.save(order);
        eventPublisher.publishOrderPrepared(order);
        log.info("action=order_prepared orderId={}", orderId);
    }

    @Override
    @Transactional
    public void markPickedUp(UUID orderId) {
        Order order = findOrder(orderId);
        order.markPickedUp();
        orderRepository.save(order);
        eventPublisher.publishOrderPickedUp(order);
        log.info("action=order_picked_up orderId={}", orderId);
    }

    @Override
    @Transactional
    public void markDelivered(UUID orderId) {
        Order order = findOrder(orderId);
        order.markDelivered();
        orderRepository.save(order);
        eventPublisher.publishOrderDelivered(order);
        log.info("action=order_delivered orderId={}", orderId);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId, UUID requestingUserId, String reason) {
        Order order = findOrder(orderId);
        order.requireOwnership(requestingUserId);
        order.cancel(reason);
        orderRepository.save(order);
        eventPublisher.publishOrderCancelled(order);
        log.info("action=order_cancelled orderId={} reason={}", orderId, reason);
    }

    @Override
    @Transactional
    public void failPayment(UUID orderId) {
        Order order = findOrder(orderId);
        if (order.getStatus() != com.gosqu.order.domain.model.OrderStatus.PENDING) {
            log.info("action=fail_payment_skipped orderId={} status={}", orderId, order.getStatus());
            return;
        }
        order.failPayment();
        orderRepository.save(order);
        log.info("action=payment_failed orderId={}", orderId);
    }

    private Order findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.id(), i.menuItemId(), i.name(),
                        i.unitPrice().amount(), i.unitPrice().currency(),
                        i.quantity(), i.subtotal().amount()))
                .toList();

        OrderResponse.DeliveryAddressResponse addr = new OrderResponse.DeliveryAddressResponse(
                order.getDeliveryAddress().street(), order.getDeliveryAddress().city(),
                order.getDeliveryAddress().postalCode(), order.getDeliveryAddress().country(),
                order.getDeliveryAddress().latitude(), order.getDeliveryAddress().longitude());

        return new OrderResponse(
                order.getId(), order.getCustomerId(), order.getRestaurantId(), order.getRestaurantName(),
                items, addr,
                order.getDeliveryFee().amount(), order.getTotalAmount().amount(),
                order.getTotalAmount().currency(),
                order.getStatus(), order.getCancellationReason(),
                order.getCreatedAt(), order.getUpdatedAt());
    }
}
