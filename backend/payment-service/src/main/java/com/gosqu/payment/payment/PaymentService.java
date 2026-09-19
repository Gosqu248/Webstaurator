package com.gosqu.payment.payment;

import com.gosqu.payment.config.PayUProperties;
import com.gosqu.payment.dto.response.PaymentResponse;
import com.gosqu.payment.enums.PaymentProvider;
import com.gosqu.payment.enums.PaymentStatus;
import com.gosqu.payment.event.PaymentCompletedEvent;
import com.gosqu.payment.event.PaymentFailedEvent;
import com.gosqu.payment.exception.PaymentNotFoundException;
import com.gosqu.payment.exception.PayUException;
import com.gosqu.payment.messaging.producer.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gosqu.payment.exception.ForbiddenException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayUClient payUClient;
    private final PayUProperties payUProperties;
    private final PaymentEventProducer eventProducer;

    @Value("${server.address:localhost}")
    private String serverAddress;

    @Value("${server.port:8085}")
    private int serverPort;

    // -----------------------------------------------------------------------
    // Kafka-driven: order.created → create PENDING payment
    // -----------------------------------------------------------------------

    @Transactional
    public void createPendingPayment(UUID orderId, UUID customerId, BigDecimal amount) {
        log.info("action=create_pending_payment orderId={} customerId={}", orderId, customerId);
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            log.warn("action=duplicate_order_event orderId={} — skipping", orderId);
            return;
        }
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setCustomerId(customerId);
        payment.setAmount(amount);
        payment.setCurrency("PLN");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProvider(PaymentProvider.PAYU); // default until customer chooses
        paymentRepository.save(payment);
    }

    // -----------------------------------------------------------------------
    // Kafka-driven: order.cancelled → refund if COMPLETED
    // -----------------------------------------------------------------------

    @Transactional
    public void handleOrderCancelled(UUID orderId) {
        log.info("action=handle_order_cancelled orderId={}", orderId);
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
                log.info("action=payment_refunded paymentId={} orderId={}", payment.getId(), orderId);
            }
        });
    }

    // -----------------------------------------------------------------------
    // REST: initiate PayU payment
    // -----------------------------------------------------------------------

    @Transactional
    public String initiatePayUPayment(UUID orderId, UUID customerId, String customerEmail) {
        log.info("action=initiate_payu orderId={}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseGet(() -> {
                    log.warn("action=no_pending_payment_found orderId={} — creating ad-hoc", orderId);
                    Payment p = new Payment();
                    p.setOrderId(orderId);
                    p.setCustomerId(customerId);
                    p.setAmount(BigDecimal.ZERO);
                    p.setCurrency("PLN");
                    p.setStatus(PaymentStatus.PENDING);
                    p.setProvider(PaymentProvider.PAYU);
                    return paymentRepository.save(p);
                });

        if (!payment.getCustomerId().equals(customerId)) {
            log.warn("action=initiate_payu_forbidden orderId={} caller={}", orderId, customerId);
            throw new ForbiddenException();
        }

        payment.setProvider(PaymentProvider.PAYU);
        paymentRepository.save(payment);

        String notifyUrl = "http://" + serverAddress + ":" + serverPort + "/payments/notify";
        PayUClient.PayUOrderResult result;
        try {
            result = payUClient.createOrder(orderId, payment.getAmount(), customerEmail, notifyUrl);
        } catch (PayUException ex) {
            log.error("action=payu_order_creation_failed orderId={}", orderId, ex);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            PaymentFailedEvent failedEvent = PaymentFailedEvent.of(payment.getId(), orderId, payment.getCustomerId(), ex.getMessage());
            eventProducer.publishPaymentFailed(failedEvent);
            throw ex;
        }

        payment.setProviderTransactionId(result.payuOrderId());
        paymentRepository.save(payment);

        return result.redirectUri();
    }

    // -----------------------------------------------------------------------
    // REST: cash payment
    // -----------------------------------------------------------------------

    @Transactional
    public PaymentResponse processCashPayment(UUID orderId, UUID customerId) {
        log.info("action=process_cash_payment orderId={}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setOrderId(orderId);
                    p.setCustomerId(customerId);
                    p.setAmount(BigDecimal.ZERO);
                    p.setCurrency("PLN");
                    p.setStatus(PaymentStatus.PENDING);
                    p.setProvider(PaymentProvider.CASH);
                    return paymentRepository.save(p);
                });

        if (!payment.getCustomerId().equals(customerId)) {
            log.warn("action=cash_payment_forbidden orderId={} caller={}", orderId, customerId);
            throw new ForbiddenException();
        }

        payment.setProvider(PaymentProvider.CASH);
        payment.setStatus(PaymentStatus.COMPLETED);
        Payment saved = paymentRepository.save(payment);

        PaymentCompletedEvent event = PaymentCompletedEvent.of(
                saved.getId(), orderId, customerId,
                saved.getAmount(), saved.getCurrency(), "CASH");
        eventProducer.publishPaymentCompleted(event);

        log.info("action=cash_payment_completed paymentId={} orderId={}", saved.getId(), orderId);
        return PaymentResponse.from(saved);
    }

    // -----------------------------------------------------------------------
    // REST: PayU IPN webhook — handle notification
    // -----------------------------------------------------------------------

    @Transactional
    public void handlePayUNotification(String requestBody, String signatureHeader) {
        log.info("action=payu_ipn_received");

        // Verify signature: MD5(body + secondKey)
        verifyPayUSignature(requestBody, signatureHeader);

        // Parse status from JSON body (simplified: look for statusCode field)
        String statusCode = extractStatusCode(requestBody);
        String payuOrderId = extractPayuOrderId(requestBody);

        if (payuOrderId == null) {
            log.warn("action=payu_ipn_no_order_id body_preview={}", requestBody.substring(0, Math.min(200, requestBody.length())));
            return;
        }
        paymentRepository.findByProviderTransactionId(payuOrderId)
                .ifPresentOrElse(payment -> {
                    switch (statusCode) {
                        case "COMPLETED" -> {
                            payment.setStatus(PaymentStatus.COMPLETED);
                            paymentRepository.save(payment);
                            PaymentCompletedEvent event = PaymentCompletedEvent.of(
                                    payment.getId(), payment.getOrderId(), payment.getCustomerId(),
                                    payment.getAmount(), payment.getCurrency(), "PAYU");
                            eventProducer.publishPaymentCompleted(event);
                            log.info("action=payu_payment_completed paymentId={}", payment.getId());
                        }
                        case "CANCELED", "REJECTED" -> {
                            payment.setStatus(PaymentStatus.FAILED);
                            paymentRepository.save(payment);
                            PaymentFailedEvent event = PaymentFailedEvent.of(
                                    payment.getId(), payment.getOrderId(), payment.getCustomerId(), "PayU status: " + statusCode);
                            eventProducer.publishPaymentFailed(event);
                            log.info("action=payu_payment_failed paymentId={} status={}", payment.getId(), statusCode);
                        }
                        default -> log.debug("action=payu_ipn_status_ignored status={}", statusCode);
                    }
                }, () -> log.warn("action=payu_ipn_no_payment_found payuOrderId={}", payuOrderId));
    }

    // -----------------------------------------------------------------------
    // REST: get payment by orderId
    // -----------------------------------------------------------------------

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(UUID orderId, UUID requestingUserId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
        if (!payment.getCustomerId().equals(requestingUserId)) {
            log.warn("action=get_payment_forbidden orderId={} caller={}", orderId, requestingUserId);
            throw new ForbiddenException();
        }
        return PaymentResponse.from(payment);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private void verifyPayUSignature(String body, String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isBlank()) {
            log.error("action=payu_signature_missing");
            throw new IllegalArgumentException("Missing PayU signature");
        }
        // Extract signature value from header: sender=checkout;signature=<hash>;algorithm=MD5
        String signature = null;
        for (String part : signatureHeader.split(";")) {
            if (part.startsWith("signature=")) {
                signature = part.substring("signature=".length());
                break;
            }
        }
        if (signature == null) {
            log.error("action=payu_signature_not_parsed header={}", signatureHeader);
            throw new IllegalArgumentException("Invalid PayU signature format");
        }
        try {
            String toHash = body + payUProperties.secondKey();
            byte[] computed = md5Bytes(toHash);
            byte[] received = signature.toLowerCase().getBytes(StandardCharsets.UTF_8);
            byte[] computedHex = toHexBytes(computed);
            if (!MessageDigest.isEqual(computedHex, received)) {
                log.error("action=payu_signature_invalid");
                throw new IllegalArgumentException("Invalid PayU signature");
            }
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("action=payu_signature_verification_error", ex);
            throw new IllegalArgumentException("PayU signature verification failed");
        }
    }

    private byte[] md5Bytes(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] toHexBytes(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String extractStatusCode(String body) {
        // Simple JSON field extraction without pulling in extra dependencies
        int idx = body.indexOf("\"statusCode\"");
        if (idx < 0) return "UNKNOWN";
        int colon = body.indexOf(':', idx);
        if (colon < 0) return "UNKNOWN";
        int start = body.indexOf('"', colon + 1);
        if (start < 0) return "UNKNOWN";
        int end = body.indexOf('"', start + 1);
        if (end < 0) return "UNKNOWN";
        return body.substring(start + 1, end);
    }

    private String extractPayuOrderId(String body) {
        // Look for PayU orderId in the notification body
        int idx = body.indexOf("\"orderId\"");
        if (idx < 0) return null;
        int colon = body.indexOf(':', idx);
        if (colon < 0) return null;
        int start = body.indexOf('"', colon + 1);
        if (start < 0) return null;
        int end = body.indexOf('"', start + 1);
        if (end < 0) return null;
        return body.substring(start + 1, end);
    }
}
