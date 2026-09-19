package com.gosqu.delivery.tracking.dto.response;

import com.gosqu.delivery.tracking.DeliveryTracking;
import org.springframework.data.geo.Distance;

import java.util.UUID;

// Osobny DTO od DeliveryTrackingResponse — /nearby dodatkowo niesie dystans do punktu wyszukiwania
// (z GeoResult), którego pojedynczy tracking (GET /orders/{id}) nie ma i mieć nie powinien.
public record NearbyDeliveryResponse(
        UUID orderId,
        UUID courierId,
        String status,
        double longitude,
        double latitude,
        double distanceKm
) {
    public static NearbyDeliveryResponse from(DeliveryTracking tracking, Distance distanceToQueryPoint) {
        return new NearbyDeliveryResponse(
                tracking.orderId(),
                tracking.courierId(),
                tracking.status().name(),
                tracking.currentLocation().getX(),
                tracking.currentLocation().getY(),
                distanceToQueryPoint.getValue()
        );
    }
}
