package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.request.AddRestaurantRequest;
import pl.urban.backend.dto.response.AddRestaurantResponse;
import pl.urban.backend.dto.response.RestaurantResponse;
import pl.urban.backend.service.RestaurantService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping("/getAll")
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @PostMapping("/addRestaurant")
    public RestaurantResponse addRestaurant(@RequestBody AddRestaurantRequest restaurant) {
        return restaurantService.addRestaurant(restaurant);
    }

    @GetMapping("/getRestaurant")
    public RestaurantResponse getRestaurant(@RequestParam Long id) {
        return restaurantService.getRestaurantById(id);
    }

    @GetMapping("/getForEdit")
    public AddRestaurantResponse getRestaurantForEdit(@RequestParam Long id) {
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
    public ResponseEntity<RestaurantResponse> updateRestaurant(@RequestParam Long id, @RequestBody AddRestaurantRequest dto) {
        try {
            RestaurantResponse updatedRestaurant = restaurantService.updateRestaurant(dto, id);
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
