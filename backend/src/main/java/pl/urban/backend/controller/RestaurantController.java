package pl.urban.backend.controller;

import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.service.RestaurantService;

import java.util.List;
import java.util.Set;
@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
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

    @GetMapping("/allDelivery")
    public List<Restaurant> getAllDeliveryRestaurants() {
        return restaurantService.getAllDeliveryRestaurants();
    }

    @GetMapping("/allPickup")
    public List<Restaurant> getAllPickupRestaurants() {
        return restaurantService.getAllPickupRestaurants();
    }

    @GetMapping("/delivery-categories")
    public Set<String> getDeliveryCategories() {
        return restaurantService.getDeliveryCategories();
    }

    @GetMapping("/pickup-categories")
    public Set<String> getPickupCategories() {
        return restaurantService.getPickupCategories();
    }

    @GetMapping("/getRestaurant")
    public Restaurant getRestaurant(@RequestParam Long id) {
        return restaurantService.getRestaurantById(id);
    }

}
