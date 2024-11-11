// OrderController.java
package pl.urban.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.OrderDTO;
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

    @GetMapping("/getOrder")
    public ResponseEntity<Order> getOrder(@RequestParam Long id) {
        try {
            Order order = orderService.getOrder(id);
            return ResponseEntity.ok(order);
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

}