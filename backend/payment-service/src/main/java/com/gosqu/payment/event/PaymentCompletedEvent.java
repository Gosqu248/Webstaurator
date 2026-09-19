package com.gosqu.payment.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(
        String eventId,
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        Instant occurredAt) {

    public static PaymentCompletedEvent of(UUID paymentId, UUID orderId, UUID customerId,
                                           BigDecimal amount, String currency, String paymentMethod) {
        return new PaymentCompletedEvent(
                UUID.randomUUID().toString(),
                paymentId, orderId, customerId,
                amount, currency, paymentMethod,
                Instant.now());
    }
}
