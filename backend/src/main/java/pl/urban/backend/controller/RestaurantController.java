package pl.urban.backend.controller;

import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.service.RestaurantService;

import java.util.List;
@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/add")
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantService.addRestaurant(restaurant);
    }

    @GetMapping("/getRestaurant")
    public Restaurant getRestaurant(@RequestParam Long id) {
        return restaurantService.getRestaurantById(id);
    }

}
