package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.response.CoordinatesResponse;
import pl.urban.backend.dto.response.RestaurantAddressResponse;
import pl.urban.backend.dto.response.SearchedRestaurantResponse;
import pl.urban.backend.service.RestaurantAddressService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants/addresses")
public class RestaurantAddressController {
    private final RestaurantAddressService restaurantAddressService;

    @GetMapping("/search")
    public ResponseEntity<List<SearchedRestaurantResponse>> searchRestaurants(
            @RequestParam String address,
            @RequestParam(defaultValue = "6") double radius) {
        List<SearchedRestaurantResponse> results = restaurantAddressService.searchNearbyRestaurants(address, radius);
        return ResponseEntity.ok(results);
    }


    @GetMapping
    public RestaurantAddressResponse getRestaurantAddress(@RequestParam Long restaurantId) {
        return restaurantAddressService.getRestaurantAddress(restaurantId);
    }

    @GetMapping("/coordinates")
    public ResponseEntity<CoordinatesResponse> getCoordinates(@RequestParam Long restaurantId) {
        return ResponseEntity.ok(restaurantAddressService.getCoordinatesByRestaurantId(restaurantId));
    }
}
