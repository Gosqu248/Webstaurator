package com.gosqu.restaurant.restaurant;

import com.gosqu.restaurant.common.exception.ForbiddenException;
import com.gosqu.restaurant.restaurant.dto.request.RestaurantRequest;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import com.gosqu.restaurant.restaurant.event.RestaurantCreatedEvent;
import com.gosqu.restaurant.restaurant.event.RestaurantDeactivatedEvent;
import com.gosqu.restaurant.restaurant.event.RestaurantUpdatedEvent;
import com.gosqu.restaurant.restaurant.exception.RestaurantNotFoundException;
import com.gosqu.restaurant.restaurant.mapper.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantEventPublisher eventPublisher;

    public Page<RestaurantResponse> search(String city, String cuisineType, String q, Pageable pageable) {
        CuisineType ct = parseCuisineType(cuisineType);
        return restaurantRepository.search(city, ct, q, pageable).map(restaurantMapper::toResponse);
    }

    @Cacheable(value = "restaurant", key = "#id")
    public RestaurantResponse getById(UUID id) {
        return restaurantMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    @CacheEvict(value = "restaurant-search", allEntries = true)
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
            @CacheEvict(value = "menu", key = "#restaurantId"),
            @CacheEvict(value = "restaurant-search", allEntries = true)
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
            @CacheEvict(value = "menu", key = "#restaurantId"),
            @CacheEvict(value = "restaurant-search", allEntries = true)
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
