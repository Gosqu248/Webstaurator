package com.gosqu.delivery.messaging.producer.dto;

import java.time.Instant;
import java.util.UUID;

public record DeliveryLocationEvent(UUID orderId, double longitude, double latitude, Instant occurredAt) {
}
