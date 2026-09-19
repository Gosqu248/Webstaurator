package com.gosqu.delivery.tracking;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Indeks 2dsphere na currentLocation jest tworzony jawnie w MongoIndexConfig.ensureGeoIndex —
// świadomie bez @GeoSpatialIndexed + auto-index-creation, żeby było jedno miejsce prawdy o indeksie.
@Document(collection = "delivery_trackings")
public record DeliveryTracking(
        @Id String id,
        UUID orderId,
        UUID customerId,
        UUID courierId,
        DeliveryStatus status,
        GeoJsonPoint currentLocation,
        List<LocationPoint> locationHistory,
        Instant estimatedArrival,
        Instant assignedAt
) {

    public static DeliveryTracking assign(UUID orderId, UUID customerId, UUID courierId, GeoJsonPoint startLocation) {
        return new DeliveryTracking(orderId.toString(), orderId, customerId, courierId,
                DeliveryStatus.ASSIGNED, startLocation, List.of(), null, Instant.now());
    }

    // Sama zmiana statusu, bez nowej pozycji GPS (np. order.prepared) — nie dopisujemy wpisu
    // do historii lokalizacji, bo lokalizacja się nie zmieniła.
    public DeliveryTracking withStatus(DeliveryStatus newStatus) {
        return new DeliveryTracking(id, orderId, customerId, courierId, newStatus, currentLocation,
                locationHistory, estimatedArrival, assignedAt);
    }

    // Nowa pozycja GPS — poprzednia lokalizacja trafia do historii przed jej nadpisaniem.
    public DeliveryTracking withLocation(GeoJsonPoint newLocation, DeliveryStatus newStatus) {
        LocationPoint historyEntry = new LocationPoint(currentLocation.getX(), currentLocation.getY(), Instant.now());
        List<LocationPoint> updatedHistory = new ArrayList<>(locationHistory);
        updatedHistory.add(historyEntry);
        return new DeliveryTracking(id, orderId, customerId, courierId, newStatus, newLocation,
                List.copyOf(updatedHistory), estimatedArrival, assignedAt);
    }
}
