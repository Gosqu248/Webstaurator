package com.gosqu.payment.dto.response;

import com.gosqu.payment.enums.PaymentProvider;
import com.gosqu.payment.enums.PaymentStatus;
import com.gosqu.payment.payment.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        PaymentProvider provider,
        String providerTransactionId,
        Instant createdAt,
        Instant updatedAt) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getProvider(),
                payment.getProviderTransactionId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }
}
