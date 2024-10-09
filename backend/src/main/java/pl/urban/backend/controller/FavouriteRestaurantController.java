package pl.urban.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.FavouriteRestaurantDTO;
import pl.urban.backend.model.FavouriteRestaurant;
import pl.urban.backend.service.FavouriteRestaurantService;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
@RequestMapping("/api/favourite")
public class FavouriteRestaurantController {

    @Autowired
    private FavouriteRestaurantService favouriteRestaurantService;

    @GetMapping("/all")
    public List<FavouriteRestaurantDTO> getAllUserFavouriteRestaurants(@RequestParam Long userId) {
        return favouriteRestaurantService.getAllUserFavouriteRestaurants(userId);
    }

    @PostMapping("/add")
    public void addFavouriteRestaurant(@RequestParam Long userId, @RequestParam Long restaurantId) {
        favouriteRestaurantService.addFavouriteRestaurant(userId, restaurantId);
    }

    @DeleteMapping("/delete")
    public void deleteFavouriteRestaurant(@RequestParam Long id) {
        favouriteRestaurantService.deleteFavouriteRestaurant(id);
    }
}
