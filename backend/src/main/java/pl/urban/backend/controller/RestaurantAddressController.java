package pl.urban.backend.controller;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.SearchedRestaurantDTO;
import pl.urban.backend.model.RestaurantAddress;
import pl.urban.backend.service.RestaurantAddressService;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantAddress")
public class RestaurantAddressController {

    private final RestaurantAddressService restaurantAddressService;


    public RestaurantAddressController(RestaurantAddressService restaurantAddressService) {
        this.restaurantAddressService = restaurantAddressService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchedRestaurantDTO>> searchRestaurants(
            @RequestParam String address,
            @RequestParam(defaultValue = "6") double radius) throws JSONException {
        List<SearchedRestaurantDTO> results = restaurantAddressService.searchNearbyRestaurants(address, radius);
        return ResponseEntity.ok(results);
    }


    @GetMapping("/get")
    public RestaurantAddress getRestaurantAddress(@RequestParam Long restaurantId) {
        return restaurantAddressService.getRestaurantAddress(restaurantId);
    }
}
