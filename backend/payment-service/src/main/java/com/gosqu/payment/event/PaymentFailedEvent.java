package com.gosqu.payment.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        String eventId,
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        String reason,
        Instant occurredAt) {

    public static PaymentFailedEvent of(UUID paymentId, UUID orderId, UUID customerId, String reason) {
        return new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                paymentId, orderId, customerId,
                reason,
                Instant.now());
    }
}
