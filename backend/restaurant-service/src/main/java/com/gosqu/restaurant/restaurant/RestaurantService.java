package com.gosqu.restaurant.restaurant;

import com.gosqu.restaurant.common.exception.ForbiddenException;
import com.gosqu.restaurant.restaurant.dto.request.RestaurantRequest;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import com.gosqu.restaurant.restaurant.event.RestaurantCreatedEvent;
import com.gosqu.restaurant.restaurant.event.RestaurantDeactivatedEvent;
import com.gosqu.restaurant.restaurant.event.RestaurantUpdatedEvent;
import com.gosqu.restaurant.restaurant.exception.RestaurantNotFoundException;
import com.gosqu.restaurant.restaurant.mapper.RestaurantMapper;
import com.gosqu.restaurant.search.RestaurantSearchResult;
import com.gosqu.restaurant.search.RestaurantSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    // Cache stampede protection (md/todo-v2/02-redis-mid-senior.md, sekcja 2) — SETNX jest atomowy w Redisie
    // (single-threaded event loop po stronie serwera), więc dwie instancje nigdy nie zdobędą tego samego locka
    // równocześnie, w przeciwieństwie do EXISTS+SET (dwie operacje = race condition).
    private static final Duration LOCK_TTL = Duration.ofSeconds(5);
    private static final Duration LOCK_WAIT_INTERVAL = Duration.ofMillis(50);
    private static final int LOCK_WAIT_ATTEMPTS = 10;

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantEventPublisher eventPublisher;
    private final RestaurantSearchService restaurantSearchService;
    private final CacheManager cacheManager;
    private final StringRedisTemplate redisTemplate;


    public Page<RestaurantResponse> search(String city, String cuisineType, String q,
                                           Double lat, Double lon, Double radiusKm,
                                           Pageable pageable) {
        CuisineType ct = parseCuisineType(cuisineType);
        RestaurantSearchResult result = restaurantSearchService.search(
                city, ct != null ? ct.name() : null, q, lat, lon, radiusKm, pageable);

        // ES daje kolejność trafności + całkowitą liczbę wyników; pełne dane restauracji
        // wciąż dociągamy z Postgresa (źródło prawdy) i odtwarzamy tę kolejność, bo
        // findAllById() jej nie gwarantuje.
        Map<UUID, Restaurant> byId = restaurantRepository.findAllById(result.orderedIds()).stream()
                .collect(Collectors.toMap(Restaurant::getId, r -> r));
        List<RestaurantResponse> ordered = result.orderedIds().stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(restaurantMapper::toResponse)
                .toList();

        return new PageImpl<>(ordered, pageable, result.totalHits());
    }

    public RestaurantResponse getById(UUID id) {
        Cache cache = cacheManager.getCache("restaurant");
        RestaurantResponse cached = cache != null ? cache.get(id, RestaurantResponse.class) : null;
        if (cached != null) {
            return cached;
        }

        String lockKey = "lock:restaurant:" + id;
        boolean lockAcquired = Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_TTL));

        if (!lockAcquired) {
            // Ktos inny juz odbudowuje ten wpis — czekamy zamiast rownolegle bic w baze (stampede).
            for (int attempt = 0; attempt < LOCK_WAIT_ATTEMPTS; attempt++) {
                sleep(LOCK_WAIT_INTERVAL);
                cached = cache != null ? cache.get(id, RestaurantResponse.class) : null;
                if (cached != null) {
                    return cached;
                }
            }
            // Lock trzymajacy proces nie zdazyl uzupelnic cache w rozsadnym czasie — czytamy z bazy
            // wprost, zamiast blokowac zadanie w nieskonczonosc.
            return restaurantMapper.toResponse(findOrThrow(id));
        }

        try {
            RestaurantResponse response = restaurantMapper.toResponse(findOrThrow(id));
            if (cache != null) {
                cache.put(id, response);
            }
            return response;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Przerwano oczekiwanie na lock cache", e);
        }
    }

    @Transactional
    public RestaurantResponse create(UUID ownerId, RestaurantRequest request) {
        Restaurant restaurant = restaurantMapper.toEntity(request);
        restaurant.setOwnerId(ownerId);
        Restaurant saved = restaurantRepository.save(restaurant);

        log.info("restaurant_created restaurantId={} ownerId={}", saved.getId(), ownerId);
        eventPublisher.publishCreated(new RestaurantCreatedEvent(
                UUID.randomUUID().toString(),
                saved.getId(), saved.getOwnerId(),
                saved.getName(), saved.getCity(),
                saved.getCuisineType().name(), Instant.now()
        ));

        return restaurantMapper.toResponse(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "restaurant", key = "#restaurantId"),
            @CacheEvict(value = "menu", key = "#restaurantId")
    })
    public RestaurantResponse update(UUID ownerId, UUID restaurantId, RestaurantRequest request) {
        Restaurant restaurant = findOrThrowOwned(ownerId, restaurantId);
        restaurantMapper.updateEntity(request, restaurant);
        Restaurant saved = restaurantRepository.save(restaurant);

        log.info("restaurant_updated restaurantId={}", restaurantId);
        eventPublisher.publishUpdated(new RestaurantUpdatedEvent(
                UUID.randomUUID().toString(),
                saved.getId(), saved.getOwnerId(),
                saved.getName(), saved.getCity(),
                saved.getCuisineType().name(), Instant.now()
        ));

        return restaurantMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "restaurant", key = "#restaurantId")
    public void updateAvgRating(UUID restaurantId, Double newRating) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException(restaurantId);
        }

        restaurantRepository.updateAvgRating(restaurantId, newRating);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "restaurant", key = "#restaurantId"),
            @CacheEvict(value = "menu", key = "#restaurantId")
    })
    public void deactivate(UUID ownerId, UUID restaurantId) {
        Restaurant restaurant = findOrThrowOwned(ownerId, restaurantId);
        restaurantRepository.deactivateById(restaurantId);

        log.info("restaurant_deactivated restaurantId={}", restaurantId);
        eventPublisher.publishDeactivated(new RestaurantDeactivatedEvent(
                UUID.randomUUID().toString(),
                restaurantId, restaurant.getOwnerId(), Instant.now()
        ));
    }

    public Restaurant findOrThrow(UUID id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }

    public UUID getOwnerId(UUID restaurantId) {
        return findOrThrow(restaurantId).getOwnerId();
    }

    public Restaurant findOrThrowOwned(UUID ownerId, UUID restaurantId) {
        Restaurant restaurant = findOrThrow(restaurantId);
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException();
        }

        return restaurant;
    }

    private CuisineType parseCuisineType(String value) {
        if (value == null) {
            return null;
        }

        try {
            return CuisineType.valueOf(value.toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown cuisine type: " + value);
        }
    }
}
