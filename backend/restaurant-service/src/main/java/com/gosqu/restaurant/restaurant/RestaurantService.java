package com.gosqu.restaurant.restaurant;

import com.gosqu.restaurant.common.exception.ForbiddenException;
import com.gosqu.restaurant.restaurant.dto.request.RestaurantRequest;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import com.gosqu.restaurant.restaurant.exception.RestaurantNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Page<RestaurantResponse> search(String city, String cuisineType, String q, Pageable pageable) {
        CuisineType ct = cuisineType != null ? CuisineType.valueOf(cuisineType.toUpperCase()) : null;
        return restaurantRepository.search(city, ct, q, pageable).map(RestaurantResponse::from);
    }

    public RestaurantResponse getById(UUID id) {
        return RestaurantResponse.from(findOrThrow(id));
    }

    @Transactional
    public RestaurantResponse create(UUID ownerId, RestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .ownerId(ownerId)
                .name(request.name())
                .description(request.description())
                .cuisineType(request.cuisineType())
                .address(request.address())
                .city(request.city())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .phoneNumber(request.phoneNumber())
                .deliveryTimeMin(request.deliveryTimeMin())
                .deliveryFee(request.deliveryFee())
                .minOrderAmount(request.minOrderAmount())
                .build();
        return RestaurantResponse.from(restaurantRepository.save(restaurant));
    }

    @Transactional
    public RestaurantResponse update(UUID ownerId, UUID restaurantId, RestaurantRequest request) {
        Restaurant restaurant = findOrThrowOwned(ownerId, restaurantId);
        restaurant.setName(request.name());
        restaurant.setDescription(request.description());
        restaurant.setCuisineType(request.cuisineType());
        restaurant.setAddress(request.address());
        restaurant.setCity(request.city());
        restaurant.setLatitude(request.latitude());
        restaurant.setLongitude(request.longitude());
        restaurant.setPhoneNumber(request.phoneNumber());
        restaurant.setDeliveryTimeMin(request.deliveryTimeMin());
        restaurant.setDeliveryFee(request.deliveryFee());
        restaurant.setMinOrderAmount(request.minOrderAmount());
        return RestaurantResponse.from(restaurantRepository.save(restaurant));
    }

    @Transactional
    public void updateAvgRating(UUID restaurantId, Double newRating) {
        Restaurant restaurant = findOrThrow(restaurantId);
        restaurant.setAvgRating(newRating);
        restaurantRepository.save(restaurant);
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
}
