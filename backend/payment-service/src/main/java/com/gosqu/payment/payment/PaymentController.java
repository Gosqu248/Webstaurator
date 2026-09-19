package com.gosqu.payment.payment;

import com.gosqu.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Initiates a PayU payment for the given order.
     * Returns a redirect URL that the customer should follow to complete the payment.
     */
    @PostMapping("/orders/{orderId}/initiate")
    public ResponseEntity<Map<String, String>> initiatePayment(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-User-Email", defaultValue = "") String userEmail,
            @PathVariable UUID orderId) {

        log.info("action=initiate_payment orderId={} userId={}", orderId, userId);
        String redirectUrl = paymentService.initiatePayUPayment(orderId, userId, userEmail);
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

    /**
     * Processes a cash-on-delivery payment for the given order.
     */
    @PostMapping("/orders/{orderId}/cash")
    public ResponseEntity<PaymentResponse> processCashPayment(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId) {

        log.info("action=process_cash_payment orderId={} userId={}", orderId, userId);
        PaymentResponse response = paymentService.processCashPayment(orderId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * PayU IPN webhook — no authentication required (PayU posts from its own servers).
     * Verifies the signature and processes the payment status update.
     */
    @PostMapping("/notify")
    public ResponseEntity<Void> handlePayUNotification(
            @RequestHeader(value = "OpenPayu-Signature", required = false) String signatureHeader,
            @RequestBody String body) {

        log.info("action=payu_notify_received");
        paymentService.handlePayUNotification(body, signatureHeader);
        return ResponseEntity.ok().build();
    }

    /**
     * Returns the payment status for the given order.
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId) {

        log.info("action=get_payment orderId={} userId={}", orderId, userId);
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId, userId);
        return ResponseEntity.ok(response);
    }
}
