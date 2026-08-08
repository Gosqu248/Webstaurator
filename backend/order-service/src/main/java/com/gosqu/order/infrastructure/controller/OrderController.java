package com.gosqu.order.infrastructure.controller;

import com.gosqu.order.application.dto.request.CreateOrderRequest;
import com.gosqu.order.application.dto.request.UpdateOrderStatusRequest;
import com.gosqu.order.application.dto.response.OrderResponse;
import com.gosqu.order.application.port.in.CreateOrderUseCase;
import com.gosqu.order.application.port.in.CreateOrderUseCase.CreateOrderCommand;
import com.gosqu.order.application.port.in.CreateOrderUseCase.OrderItemCommand;
import com.gosqu.order.application.port.in.GetOrderUseCase;
import com.gosqu.order.application.port.in.UpdateOrderStatusUseCase;
import com.gosqu.order.domain.model.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid CreateOrderRequest request) {

        CreateOrderCommand command = new CreateOrderCommand(
                userId,
                request.restaurantId(),
                request.items().stream()
                        .map(i -> new OrderItemCommand(i.menuItemId(), i.quantity()))
                        .toList());

        OrderResponse response = createOrderUseCase.createOrder(command);
        URI location = URI.create("/orders/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId) {
        return getOrderUseCase.getOrder(orderId, userId);
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(@RequestHeader("X-User-Id") UUID userId) {
        return getOrderUseCase.getOrdersForCustomer(userId);
    }

    // TODO: verify caller is owner/staff of restaurantId before returning orders
    //       requires restaurant-service to expose GET /restaurants/{id}/owner or similar
    @GetMapping("/restaurant/{restaurantId}")
    public List<OrderResponse> getOrdersForRestaurant(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID restaurantId) {
        log.debug("action=get_orders_for_restaurant restaurantId={} requestedBy={}", restaurantId, userId);
        return getOrderUseCase.getOrdersForRestaurant(restaurantId);
    }

    // TODO: PREPARING/PREPARED require restaurant owner/staff role;
    //       PICKED_UP/DELIVERED require assigned courier role.
    //       Full role check needs restaurant ownership lookup from restaurant-service.
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateStatus(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId,
            @RequestBody @Valid UpdateOrderStatusRequest request) {

        OrderStatus target = request.status();
        switch (target) {
            case PREPARING  -> updateOrderStatusUseCase.startPreparing(orderId);
            case PREPARED   -> updateOrderStatusUseCase.markPrepared(orderId);
            case PICKED_UP  -> updateOrderStatusUseCase.markPickedUp(orderId);
            case DELIVERED  -> updateOrderStatusUseCase.markDelivered(orderId);
            case CANCELLED  -> updateOrderStatusUseCase.cancelOrder(orderId, userId,
                    request.cancellationReason());
            default -> throw new IllegalArgumentException("Status transition to " + target + " not allowed via API");
        }
        return ResponseEntity.noContent().build();
    }
}
