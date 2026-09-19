package com.gosqu.delivery.tracking;

import com.gosqu.delivery.common.exception.ForbiddenException;
import com.gosqu.delivery.tracking.dto.request.LocationUpdateRequest;
import com.gosqu.delivery.tracking.dto.response.DeliveryTrackingResponse;
import com.gosqu.delivery.tracking.dto.response.NearbyDeliveryResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
@Validated
public class DeliveryController {

    private static final String ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN";

    private final DeliveryTrackingService deliveryTrackingService;

    // X-User-Id/X-User-Role: nagłówki wstrzykiwane przez api-gateway na podstawie zweryfikowanego
    // JWT (gateway usuwa te same nagłówki, gdyby klient próbował je podstawić). Faktyczna kontrola
    // dostępu (czy TEN konkretny użytkownik ma prawo widzieć/edytować TĘ konkretną dostawę) i tak
    // musi się odbyć tutaj, bo sam ważny JWT nie mówi nic o właścicielstwie zasobu — stąd sprawdzenie
    // w DeliveryTrackingService, a nie tylko poleganie na roli z nagłówka.
    @GetMapping("/orders/{orderId}")
    public DeliveryTrackingResponse getByOrderId(@RequestHeader("X-User-Id") UUID userId,
                                                  @RequestHeader(value = "X-User-Role", required = false) String role,
                                                  @PathVariable UUID orderId) {
        return deliveryTrackingService.getByOrderId(orderId, userId, role);
    }

    @PostMapping("/orders/{orderId}/location")
    public DeliveryTrackingResponse updateLocation(@RequestHeader("X-User-Id") UUID userId,
                                                     @PathVariable UUID orderId,
                                                     @RequestBody @Valid LocationUpdateRequest request) {
        return deliveryTrackingService.updateLocation(orderId, userId, request);
    }

    @GetMapping("/nearby")
    public List<NearbyDeliveryResponse> findNearby(@RequestHeader("X-User-Role") String role,
                                                     @RequestParam @DecimalMin("-90") @DecimalMax("90") double lat,
                                                     @RequestParam @DecimalMin("-180") @DecimalMax("180") double lon,
                                                     @RequestParam @DecimalMin("0.1") @DecimalMax("50") double radiusKm) {
        requireRole(role);
        return deliveryTrackingService.findNearby(lat, lon, radiusKm);
    }

    private void requireRole(String actual) {
        if (!DeliveryController.ROLE_SYSTEM_ADMIN.equals(actual)) {
            throw new ForbiddenException();
        }
    }
}
