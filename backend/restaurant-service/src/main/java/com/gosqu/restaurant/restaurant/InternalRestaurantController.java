package com.gosqu.restaurant.restaurant;

import com.gosqu.restaurant.restaurant.dto.response.RestaurantOwnerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/restaurants")
@RequiredArgsConstructor
class InternalRestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/{id}/owner")
    public RestaurantOwnerResponse getOwner(@PathVariable UUID id) {
        return new RestaurantOwnerResponse(restaurantService.getOwnerId(id));
    }
}
