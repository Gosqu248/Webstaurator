package pl.urban.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Order;
import pl.urban.backend.model.Payment;
import pl.urban.backend.service.PayUService;
import pl.urban.backend.service.PaymentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PayUService payUService;

    public PaymentController(PaymentService paymentService, PayUService payUService) {
        this.paymentService = paymentService;
        this.payUService = payUService;
    }

    @GetMapping("/getAll")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/getRestaurantPayments")
    public List<Payment> getAllPaymentForRestaurant(@RequestParam Long restaurantId) {
        return paymentService.getALlPaymentsByRestaurantId(restaurantId);
    }

    @PostMapping("/createPayment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody Order order) {
        try {
            String redirectUrl = payUService.createOrder(order);
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}