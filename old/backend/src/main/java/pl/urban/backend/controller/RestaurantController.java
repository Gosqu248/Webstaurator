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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/restaurant")
    public RestaurantResponse getRestaurant(@RequestParam Long id) {
        return restaurantService.getRestaurantById(id);
    }

    @GetMapping("/edit")
    public AddRestaurantResponse getRestaurantForEdit(@RequestParam Long id) {
        return restaurantService.getRestaurantForEdit(id);
    }

    @GetMapping("/logo")
    public String getLogo(@RequestParam Long id) {
        return restaurantService.getLogo(id);
    }

    @PostMapping
    public RestaurantResponse addRestaurant(@RequestBody AddRestaurantRequest restaurant) {
        return restaurantService.addRestaurant(restaurant);
    }

    @PutMapping
    public ResponseEntity<RestaurantResponse> updateRestaurant(@RequestParam Long id, @RequestBody AddRestaurantRequest dto) {
        try {
            RestaurantResponse updatedRestaurant = restaurantService.updateRestaurant(dto, id);
            return ResponseEntity.ok(updatedRestaurant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> removeRestaurant(@RequestParam Long id) {
        try {
            restaurantService.removeRestaurant(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
