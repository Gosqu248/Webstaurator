// OrderController.java
package pl.urban.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.AdminOrderDTO;
import pl.urban.backend.dto.OrderDTO;
import pl.urban.backend.enums.OrderStatus;
import pl.urban.backend.model.Order;
import pl.urban.backend.security.JwtUtil;
import pl.urban.backend.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtToken;


    public OrderController(OrderService orderService, JwtUtil jwtToken) {
        this.orderService = orderService;
        this.jwtToken = jwtToken;
    }


    @GetMapping("/getAllOrders")
    public ResponseEntity<List<AdminOrderDTO>> getAllOrders() {
        try {
            List<AdminOrderDTO> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getUserOrders")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@RequestHeader("Authorization") String token) {
        try {
            String subject = jwtToken.extractSubjectFromToken(token.substring(7));
            List<OrderDTO> orders = orderService.getUserOrders(subject);
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