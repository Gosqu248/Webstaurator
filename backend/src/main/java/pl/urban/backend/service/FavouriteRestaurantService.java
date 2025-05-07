package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.response.FavouriteRestaurantResponse;
import pl.urban.backend.dto.response.OpinionResponse;
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

    public List<FavouriteRestaurantResponse> getAllUserFavouriteRestaurants(Long userId) {
        List<FavouriteRestaurant> favouriteRestaurants = favouriteRestaurantRepository.findByUserId(userId);
        return favouriteRestaurants.stream()
                .map(this::fromFavouriteRestaurant)
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

    public FavouriteRestaurantResponse fromFavouriteRestaurant(FavouriteRestaurant favouriteRestaurant) {
        Restaurant restaurant = favouriteRestaurant.getRestaurant();
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found for favourite restaurant with ID: " + favouriteRestaurant.getId());
        }
        RestaurantAddress address = restaurant.getRestaurantAddress();
        if (address == null) {
           throw new IllegalArgumentException("Restaurant address not found for restaurant with ID: " + restaurant.getId());
        }

        List<OpinionResponse> opinions = restaurant.getRestaurantOpinions().stream()
                    .map(restaurantOpinion -> new OpinionResponse(
                            restaurantOpinion.getId(),
                            restaurantOpinion.getQualityRating(),
                            restaurantOpinion.getDeliveryRating()
                    ))
                    .collect(Collectors.toList());

        return new FavouriteRestaurantResponse(
                favouriteRestaurant.getId(),
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getLogoUrl(),
                address.getStreet(),
                address.getFlatNumber(),
                opinions
        );

    }
}
