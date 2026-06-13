package com.gosqu.restaurant.restaurant;

import com.gosqu.restaurant.common.exception.ForbiddenException;
import com.gosqu.restaurant.restaurant.dto.request.RestaurantRequest;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public Page<RestaurantResponse> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") @Max(100) int size) {
        return restaurantService.search(city, cuisineType, q, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public RestaurantResponse getById(@PathVariable UUID id) {
        return restaurantService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@RequestHeader("X-User-Id") UUID ownerId,
                                     @RequestBody @Valid RestaurantRequest request) {
        return restaurantService.create(ownerId, request);
    }

    @PutMapping("/{id}")
    public RestaurantResponse update(@RequestHeader("X-User-Id") UUID ownerId,
                                     @PathVariable UUID id,
                                     @RequestBody @Valid RestaurantRequest request) {
        return restaurantService.update(ownerId, id, request);
    }

    @PutMapping("/{id}/rating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRating(@PathVariable UUID id,
                              @RequestParam @DecimalMin("0.0") @DecimalMax("5.0") Double rating,
                              @RequestHeader("X-Internal-Service") String internalService) {
        if (!"review-service".equals(internalService)) {
            throw new ForbiddenException();
        }
        restaurantService.updateAvgRating(id, rating);
    }
}
