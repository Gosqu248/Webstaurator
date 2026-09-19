package com.gosqu.restaurant.restaurant;

import com.gosqu.restaurant.common.exception.ForbiddenException;
import com.gosqu.restaurant.restaurant.dto.request.RestaurantRequest;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Validated
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public Page<RestaurantResponse> search(
            @RequestParam(required = false) @Size(max = 100) String city,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) @Size(max = 100) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@RequestHeader("X-User-Id") UUID ownerId,
                           @PathVariable UUID id) {
        restaurantService.deactivate(ownerId, id);
    }
}
