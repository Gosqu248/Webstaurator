// OrderController.java
package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.response.AdminOrderResponse;
import pl.urban.backend.dto.response.OrderResponse;
import pl.urban.backend.model.Order;
import pl.urban.backend.config.security.JwtUtil;
import pl.urban.backend.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtToken;

    @GetMapping("/getAllOrders")
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders() {
        try {
            List<AdminOrderResponse> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getUserOrders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@RequestHeader("Authorization") String token) {
        try {
            String subject = jwtToken.extractSubjectFromToken(token.substring(7));
            List<OrderResponse> orders = orderService.getUserOrders(subject);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/createOrder")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            throw e;
        }
    }

    @PutMapping("/updateOrderStatus")
    public ResponseEntity<Order> updateOrderStatus(@RequestParam Long orderId) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            throw e;
        }
    }

}
