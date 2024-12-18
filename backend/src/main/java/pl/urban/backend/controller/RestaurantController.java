package pl.urban.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.AddRestaurantDTO;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.service.RestaurantService;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/addRestaurant")
    public Restaurant addRestaurant(@RequestBody AddRestaurantDTO restaurant) {
        return restaurantService.addRestaurant(restaurant);
    }

    @GetMapping("/getRestaurant")
    public Restaurant getRestaurant(@RequestParam Long id) {
        return restaurantService.getRestaurantById(id);
    }

    @GetMapping("/getLogo")
    public String getLogo(@RequestParam Long id) {
        return restaurantService.getLogo(id);
    }

    @PutMapping("/updateRestaurant")
    public ResponseEntity<Restaurant> updateRestaurant(@RequestParam Long id, @RequestBody Restaurant restaurantDetails) {
        try {
            Restaurant updatedRestaurant = restaurantService.updateRestaurant(id, restaurantDetails);
            return ResponseEntity.ok(updatedRestaurant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
