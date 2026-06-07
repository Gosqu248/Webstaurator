package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.response.FavouriteRestaurantResponse;
import pl.urban.backend.dto.request.FavouriteRequest;
import pl.urban.backend.service.FavouriteRestaurantService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favourites")
public class FavouriteRestaurantController {
    private final FavouriteRestaurantService favouriteRestaurantService;

    @GetMapping
    public List<FavouriteRestaurantResponse> getAllUserFavouriteRestaurants(@RequestParam Long userId) {
        return favouriteRestaurantService.getAllUserFavouriteRestaurants(userId);
    }

    @PostMapping
    public void addFavouriteRestaurant(@RequestBody FavouriteRequest favouriteRequest) {
        favouriteRestaurantService.addFavouriteRestaurant(favouriteRequest.userId(), favouriteRequest.restaurantId());
    }

    @DeleteMapping
    public void deleteFavouriteRestaurant(@RequestBody FavouriteRequest favouriteRequest) {
        favouriteRestaurantService.deleteFavouriteRestaurant(favouriteRequest.userId(), favouriteRequest.restaurantId());
    }

    @GetMapping("/isFavourite")
    public boolean isFavouriteRestaurant(@RequestParam Long userId, @RequestParam Long restaurantId) {
        return favouriteRestaurantService.isFavouriteRestaurant(userId, restaurantId);
    }
}
