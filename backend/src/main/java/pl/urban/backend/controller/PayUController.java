package pl.urban.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.request.OrderRequest;
import pl.urban.backend.service.PayUService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payU")
public class PayUController {
    private final PayUService payUService;

    @GetMapping("/generateAuthHeader")
    public ResponseEntity<String> getTokens() {
        String tokenResponse = payUService.getOAuthToken();
        return ResponseEntity.ok(tokenResponse);
    }
    @PostMapping("/createPayment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody OrderRequest order, HttpServletRequest request) {
        try {
            String ip = request.getRemoteAddr();
            Map<String, String> result = payUService.createOrder(order, ip);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getPaymentStatus")
    public ResponseEntity<String> getPayUOrderStatus(@RequestParam String orderId) {
        try {
            String status = payUService.getOrderStatus(orderId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
