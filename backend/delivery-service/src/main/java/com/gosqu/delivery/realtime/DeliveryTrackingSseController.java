package com.gosqu.delivery.realtime;

import com.gosqu.delivery.tracking.DeliveryTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeliveryTrackingSseController {

    private final DeliveryTrackingBroadcaster broadcaster;
    private final DeliveryTrackingService deliveryTrackingService;

    // Ta sama kontrola dostępu co GET /delivery/orders/{orderId} — bez niej dowolny zalogowany
    // użytkownik znający orderId mógłby podsłuchiwać cudzy strumień pozycji GPS.
    @GetMapping(value = "/delivery/orders/{orderId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestHeader("X-User-Id") UUID userId,
                                 @RequestHeader(value = "X-User-Role", required = false) String role,
                                 @PathVariable UUID orderId) {
        deliveryTrackingService.assertReadAccess(orderId, userId, role);
        return broadcaster.subscribe(orderId);
    }
}
