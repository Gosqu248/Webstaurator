package pl.urban.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.AddRestaurantDTO;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.service.RestaurantService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/getAll")
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @PostMapping("/addRestaurant")
    public Restaurant addRestaurant(@RequestBody AddRestaurantDTO restaurant) {
        return restaurantService.addRestaurant(restaurant);
    }

    @GetMapping("/getRestaurant")
    public Restaurant getRestaurant(@RequestParam Long id) {
        return restaurantService.getRestaurantById(id);
    }

    @GetMapping("/getForEdit")
    public AddRestaurantDTO getRestaurantForEdit(@RequestParam Long id) {
        return restaurantService.getRestaurantForEdit(id);
    }

    @GetMapping("/getLogo")
    public String getLogo(@RequestParam Long id) {
        return restaurantService.getLogo(id);
    }

    @GetMapping("/getLogoAndImage")
    public ResponseEntity<Map<String, String>> getLogoAndImage(@RequestParam Long id) {
        try {
            Map<String, String> logoAndImage = restaurantService.getLogoAndImage(id);
            return ResponseEntity.ok(logoAndImage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Restaurant> updateRestaurant(@RequestParam Long id, @RequestBody AddRestaurantDTO dto) {
        try {
            Restaurant updatedRestaurant = restaurantService.updateRestaurant(dto, id);
            return ResponseEntity.ok(updatedRestaurant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeRestaurant(@RequestParam Long id) {
        try {
            restaurantService.removeRestaurant(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
