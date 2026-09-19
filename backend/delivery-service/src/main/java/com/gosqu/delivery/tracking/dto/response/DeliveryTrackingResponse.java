package com.gosqu.delivery.tracking.dto.response;

import com.gosqu.delivery.tracking.DeliveryTracking;

import java.time.Instant;
import java.util.UUID;

public record DeliveryTrackingResponse(
        UUID orderId,
        UUID courierId,
        String status,
        double longitude,
        double latitude,
        Instant estimatedArrival
) {
    public static DeliveryTrackingResponse from(DeliveryTracking tracking) {
        return new DeliveryTrackingResponse(
                tracking.orderId(),
                tracking.courierId(),
                tracking.status().name(),
                tracking.currentLocation().getX(),
                tracking.currentLocation().getY(),
                tracking.estimatedArrival()
        );
    }
}
