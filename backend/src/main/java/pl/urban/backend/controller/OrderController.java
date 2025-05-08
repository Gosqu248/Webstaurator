// OrderController.java
package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.request.OrderRequest;
import pl.urban.backend.dto.response.AdminOrderResponse;
import pl.urban.backend.dto.response.OrderResponse;
import pl.urban.backend.model.Order;
import pl.urban.backend.config.security.JwtUtil;
import pl.urban.backend.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Slf4j // Add logging capability
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtToken;

    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<AdminOrderResponse> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching all orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching orders: " + e.getMessage());
        }
    }

    @GetMapping("/getUserOrders")
    public ResponseEntity<?> getUserOrders(@RequestHeader("Authorization") String token) {
        try {
            // Validate token format
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid authorization token format");
            }

            String subject = jwtToken.extractSubject(token.substring(7));
            List<OrderResponse> orders = orderService.getUserOrders(subject);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            log.error("User validation error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching user orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest order) {
        try {
            OrderResponse createdOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException e) {
            log.error("Order validation error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid order data: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/updateOrderStatus")
    public ResponseEntity<?> updateOrderStatus(@RequestParam Long orderId) {
        try {
            if (orderId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Order ID is required");
            }

            OrderResponse updatedOrder = orderService.updateOrderStatus(orderId);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            log.error("Order not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Order not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error updating order status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating order status: " + e.getMessage());
        }
    }
}
