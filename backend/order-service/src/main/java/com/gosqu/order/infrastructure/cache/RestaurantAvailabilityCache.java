package com.gosqu.order.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * Szybki, fail-open pre-check dostępności restauracji. Ustawiany przez
 * {@link com.gosqu.order.infrastructure.messaging.consumer.RestaurantEventConsumer} po evencie
 * restaurant.deactivated. TTL samo-naprawia flagę, dopóki nie powstanie konsument restaurant.updated (Część 1.2).
 * Brak wpisu w Redis lub błąd Redis = restauracja uznawana za dostępną — ostateczną prawdą pozostaje
 * synchroniczne wywołanie REST w {@link com.gosqu.order.infrastructure.client.RestaurantClientAdapter}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantAvailabilityCache {

    // 5 minut, nie dłużej — nic nie czyści tego klucza przy ponownej aktywacji restauracji
    // (konsument dla restaurant.updated/reaktywacji jest świadomie poza zakresem, Część 1.2),
    // więc zbyt długi TTL odrzucałby zamówienia dla restauracji, która w międzyczasie wróciła
    // do działania, mimo że REST (ostateczna prawda) już by to potwierdził.
    private static final Duration UNAVAILABLE_TTL = Duration.ofMinutes(5);
    private static final String KEY_PREFIX = "restaurant:unavailable:";

    private final StringRedisTemplate redisTemplate;

    public void markUnavailable(UUID restaurantId) {
        try {
            redisTemplate.opsForValue().set(key(restaurantId), "1", UNAVAILABLE_TTL);
        } catch (Exception ex) {
            log.warn("action=restaurant_availability_cache_write_failed restaurantId={} cause={}", restaurantId, ex.toString());
        }
    }

    public boolean isUnavailable(UUID restaurantId) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key(restaurantId)));
        } catch (Exception ex) {
            log.warn("action=restaurant_availability_cache_read_failed restaurantId={} cause={}", restaurantId, ex.toString());
            return false;
        }
    }

    private String key(UUID restaurantId) {
        return KEY_PREFIX + restaurantId;
    }
}
