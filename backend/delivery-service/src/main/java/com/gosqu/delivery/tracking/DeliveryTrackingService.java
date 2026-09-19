package com.gosqu.delivery.tracking;

import com.gosqu.delivery.common.exception.ForbiddenException;
import com.gosqu.delivery.courier.CourierAssignmentService;
import com.gosqu.delivery.messaging.producer.DeliveryLocationPublisher;
import com.gosqu.delivery.messaging.producer.dto.DeliveryLocationEvent;
import com.gosqu.delivery.realtime.DeliveryTrackingBroadcaster;
import com.gosqu.delivery.tracking.dto.request.LocationUpdateRequest;
import com.gosqu.delivery.tracking.dto.response.DeliveryTrackingResponse;
import com.gosqu.delivery.tracking.dto.response.NearbyDeliveryResponse;
import com.gosqu.delivery.tracking.exception.DeliveryTrackingNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryTrackingService {

    private static final String ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN";

    // Kurier startuje z (0,0), dopóki nie dociągamy realnych współrzędnych restauracji
    // (REST do restaurant-service albo dodanie restaurantId do OrderConfirmedEvent) —
    // świadome uproszczenie z przewodnika, nadpisywane pierwszym prawdziwym POST /location.
    private static final GeoJsonPoint PLACEHOLDER_START_LOCATION = new GeoJsonPoint(0, 0);

    private final DeliveryTrackingRepository deliveryTrackingRepository;
    private final CourierAssignmentService courierAssignmentService;
    private final DeliveryLocationPublisher deliveryLocationPublisher;
    private final DeliveryTrackingBroadcaster broadcaster;

    public DeliveryTrackingResponse getByOrderId(UUID orderId, UUID requesterId, String requesterRole) {
        DeliveryTracking tracking = findOrThrow(orderId);
        requireParticipantOrAdmin(tracking, requesterId, requesterRole);
        return DeliveryTrackingResponse.from(tracking);
    }

    // Wywoływane też przez SSE controller przed otwarciem strumienia — ten sam próg dostępu
    // co przy zwykłym GET, żeby nie dało się obejść autoryzacji przez subskrypcję zamiast odczytu.
    public void assertReadAccess(UUID orderId, UUID requesterId, String requesterRole) {
        requireParticipantOrAdmin(findOrThrow(orderId), requesterId, requesterRole);
    }

    public void assignForOrder(UUID orderId, UUID customerId) {
        UUID courierId = courierAssignmentService.assignCourier(orderId);
        deliveryTrackingRepository.save(
                DeliveryTracking.assign(orderId, customerId, courierId, PLACEHOLDER_START_LOCATION));
        log.info("action=delivery_assigned orderId={} courierId={}", orderId, courierId);
    }

    public void markAtRestaurant(UUID orderId) {
        DeliveryTracking tracking = findOrThrow(orderId);
        deliveryTrackingRepository.save(tracking.withStatus(DeliveryStatus.AT_RESTAURANT));
        log.info("action=delivery_at_restaurant orderId={}", orderId);
    }

    public DeliveryTrackingResponse updateLocation(UUID orderId, UUID requesterId, LocationUpdateRequest request) {
        DeliveryTracking tracking = findOrThrow(orderId);
        requireAssignedCourier(tracking, requesterId);

        GeoJsonPoint newLocation = new GeoJsonPoint(request.longitude(), request.latitude());
        DeliveryTracking updated = tracking.withLocation(newLocation, nextStatus(tracking.status()));
        deliveryTrackingRepository.save(updated);

        // Trzy efekty uboczne jednej aktualizacji pozycji: zapis w Mongo (wyżej), event na Kafkę
        // (dla order-service) i push do subskrybentów SSE — typowy wzorzec event-driven.
        deliveryLocationPublisher.publish(new DeliveryLocationEvent(
                orderId, request.longitude(), request.latitude(), Instant.now()));
        broadcaster.publish(orderId, DeliveryTrackingResponse.from(updated));

        return DeliveryTrackingResponse.from(updated);
    }

    public List<NearbyDeliveryResponse> findNearby(double lat, double lon, double radiusKm) {
        GeoJsonPoint center = new GeoJsonPoint(lon, lat);
        Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);

        return deliveryTrackingRepository.findByCurrentLocationNear(center, radius)
                .getContent().stream()
                .map(result -> NearbyDeliveryResponse.from(result.getContent(), result.getDistance()))
                .toList();
    }

    private DeliveryTracking findOrThrow(UUID orderId) {
        return deliveryTrackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryTrackingNotFoundException(orderId));
    }

    // Odczyt: klient zamówienia, przypisany kurier albo SYSTEM_ADMIN — każdy z nich ma legalny
    // powód, żeby widzieć postęp dostawy.
    private void requireParticipantOrAdmin(DeliveryTracking tracking, UUID requesterId, String requesterRole) {
        boolean isParticipant = requesterId != null
                && (requesterId.equals(tracking.courierId()) || requesterId.equals(tracking.customerId()));
        boolean isAdmin = ROLE_SYSTEM_ADMIN.equals(requesterRole);
        if (!isParticipant && !isAdmin) {
            throw new ForbiddenException();
        }
    }

    // Zapis pozycji GPS: tylko przypisany kurier — nawet SYSTEM_ADMIN nie powinien móc "teleportować"
    // cudzej dostawy przez ten endpoint, to inna klasa operacji niż sam odczyt.
    private void requireAssignedCourier(DeliveryTracking tracking, UUID requesterId) {
        if (requesterId == null || !requesterId.equals(tracking.courierId())) {
            throw new ForbiddenException();
        }
    }

    // AT_RESTAURANT -> EN_ROUTE_TO_CUSTOMER następuje przy pierwszej aktualizacji pozycji po
    // przygotowaniu zamówienia. Przejście do DELIVERED wymagałoby osobnego sygnału (np. konsumpcji
    // order.picked-up/order.delivered), którego ten serwis na razie nie subskrybuje — poza zakresem
    // przewodnika (sekcja 4).
    private DeliveryStatus nextStatus(DeliveryStatus current) {
        return current == DeliveryStatus.AT_RESTAURANT ? DeliveryStatus.EN_ROUTE_TO_CUSTOMER : current;
    }
}
