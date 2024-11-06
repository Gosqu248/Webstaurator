package pl.urban.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Order;
import pl.urban.backend.model.Payment;
import pl.urban.backend.service.PayUService;
import pl.urban.backend.service.PaymentService;
import pl.urban.backend.service.TpayService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PayUService payUService;

    private final TpayService tpayService;

    public PaymentController(PaymentService paymentService, PayUService payUService, TpayService tpayService) {
        this.paymentService = paymentService;
        this.payUService = payUService;
        this.tpayService = tpayService;
    }

    @GetMapping("/getAll")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/getRestaurantPayments")
    public List<Payment> getAllPaymentForRestaurant(@RequestParam Long restaurantId) {
        return paymentService.getALlPaymentsByRestaurantId(restaurantId);
    }

    @PostMapping("/createPayUPayment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody Order order, HttpServletRequest request) {
        try {
            String ip = request.getRemoteAddr();
            String redirectUrl = payUService.createOrder(order, ip);
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/generatePayUAuthHeader")
    public ResponseEntity<String> getTokens() {
        String tokenResponse = payUService.getOAuthToken();
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/generateAuthHeader")
    public ResponseEntity<Map<String, Object>> getToken() {
        Map<String, Object> tokenResponse = tpayService.requestToken();
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/getPayUOrder")
    public ResponseEntity<String> getPayUOrderById(@RequestParam String orderId) {
        try {
            JSONObject orderDetails = payUService.getOrderById(orderId);
            return ResponseEntity.ok(orderDetails.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}