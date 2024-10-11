package pl.urban.backend.controller;

import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.RestaurantAddress;
import pl.urban.backend.service.RestaurantAddressService;

@RestController
@RequestMapping("/api/restaurantAddress")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class RestaurantAddressController {

    private final RestaurantAddressService restaurantAddressService;

    public RestaurantAddressController(RestaurantAddressService restaurantAddressService) {
        this.restaurantAddressService = restaurantAddressService;
    }

    @GetMapping("/get")
    public RestaurantAddress getRestaurantAddress(@RequestParam Long restaurantId) {
        return restaurantAddressService.getRestaurantAddress(restaurantId);
    }
}
