package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.response.FavouriteRestaurantResponse;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.FavouriteRestaurantRepository;
import pl.urban.backend.repository.RestaurantRepository;
import pl.urban.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavouriteRestaurantService {
    private final FavouriteRestaurantRepository favouriteRestaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MapperService mapper;

    public List<FavouriteRestaurantResponse> getAllUserFavouriteRestaurants(Long userId) {
        return favouriteRestaurantRepository.findByUserId(userId).stream()
                .map(mapper::fromFavouriteRestaurant)
                .collect(Collectors.toList());
    }

    public void addFavouriteRestaurant(Long userId, Long restaurantId) {
        try {
            FavouriteRestaurant favouriteRestaurant = getFavouriteRestaurant(userId, restaurantId);
            favouriteRestaurantRepository.save(favouriteRestaurant);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Add Invalid user or restaurant ID");
        }
    }

    public void deleteFavouriteRestaurant(Long userId, Long restaurantId) {
        try {
            FavouriteRestaurant favouriteRestaurant = favouriteRestaurantRepository.findFirstByUserIdAndRestaurantId(userId, restaurantId)
                    .orElseThrow(() -> new IllegalArgumentException("Favourite not found"));
            favouriteRestaurantRepository.delete(favouriteRestaurant);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Delete Invalid user or restaurant ID");
        }
    }

    public boolean isFavouriteRestaurant(Long userId, Long restaurantId) {
        return favouriteRestaurantRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }

    private FavouriteRestaurant getFavouriteRestaurant(Long userId, Long restaurantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant ID"));
        FavouriteRestaurant favouriteRestaurant = new FavouriteRestaurant();
        favouriteRestaurant.setUser(user);
        favouriteRestaurant.setRestaurant(restaurant);
        return favouriteRestaurant;
    }

}
