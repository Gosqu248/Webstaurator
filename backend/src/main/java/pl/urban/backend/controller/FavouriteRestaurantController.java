package pl.urban.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.FavouriteRestaurantDTO;
import pl.urban.backend.request.FavouriteRequest;
import pl.urban.backend.service.FavouriteRestaurantService;

import java.util.List;

@RestController
@RequestMapping("/api/favourite")
public class FavouriteRestaurantController {

    private final FavouriteRestaurantService favouriteRestaurantService;

    public FavouriteRestaurantController(FavouriteRestaurantService favouriteRestaurantService) {
        this.favouriteRestaurantService = favouriteRestaurantService;
    }

    @GetMapping("/all")
    public List<FavouriteRestaurantDTO> getAllUserFavouriteRestaurants(@RequestParam Long userId) {
        return favouriteRestaurantService.getAllUserFavouriteRestaurants(userId);
    }

    @PostMapping("/add")
    public void addFavouriteRestaurant(@RequestBody FavouriteRequest favouriteRequest) {
        favouriteRestaurantService.addFavouriteRestaurant(favouriteRequest.getUserId(), favouriteRequest.getRestaurantId());
    }

    @DeleteMapping("/delete")
    public void deleteFavouriteRestaurant(@RequestBody FavouriteRequest favouriteRequest) {
        favouriteRestaurantService.deleteFavouriteRestaurant(favouriteRequest.getUserId(), favouriteRequest.getRestaurantId());
    }

    @GetMapping("/isFavourite")
    public boolean isFavouriteRestaurant(@RequestParam Long userId, @RequestParam Long restaurantId) {
        return favouriteRestaurantService.isFavouriteRestaurant(userId, restaurantId);
    }
}
