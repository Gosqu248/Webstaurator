package com.gosqu.delivery.realtime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

// Rejestr subskrybentów SSE wydzielony z kontrolera do osobnego komponentu — DeliveryTrackingService
// (warstwa serwisowa) publikuje przez ten komponent, a nie przez wstrzykiwanie @RestController,
// bo zależność service -> controller odwraca zwykły kierunek warstw (controller -> service).
// Rejestr żyje tylko w pamięci JEDNEJ instancji; przy >1 replice trzeba by przejść na Redis Pub/Sub
// albo Kafkę jako fan-out między instancjami (nieistotne, dopóki serwis nie jest skalowany poziomo).
@Slf4j
@Component
public class DeliveryTrackingBroadcaster {

    private static final long NO_TIMEOUT = 0L;

    private final Map<UUID, List<SseEmitter>> emittersByOrderId = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID orderId) {
        SseEmitter emitter = new SseEmitter(NO_TIMEOUT);
        emittersByOrderId.computeIfAbsent(orderId, id -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(orderId, emitter));
        emitter.onTimeout(() -> removeEmitter(orderId, emitter));
        return emitter;
    }

    public void publish(UUID orderId, Object payload) {
        emittersByOrderId.getOrDefault(orderId, List.of())
                .forEach(emitter -> sendOrDrop(orderId, emitter, payload));
    }

    private void sendOrDrop(UUID orderId, SseEmitter emitter, Object payload) {
        try {
            emitter.send(SseEmitter.event().name("location").data(payload));
        } catch (Exception _) {
            log.debug("action=sse_emitter_closed orderId={}", orderId);
            emitter.complete();
            removeEmitter(orderId, emitter);
        }
    }

    private void removeEmitter(UUID orderId, SseEmitter emitter) {
        emittersByOrderId.getOrDefault(orderId, List.of()).remove(emitter);
    }
}
