package pl.urban.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.FavouriteRestaurantDTO;
import pl.urban.backend.dto.OpinionDTO;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.FavouriteRestaurantRepository;
import pl.urban.backend.repository.RestaurantRepository;
import pl.urban.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavouriteRestaurantService {

    @Autowired
    private FavouriteRestaurantRepository favouriteRestaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<FavouriteRestaurantDTO> getAllUserFavouriteRestaurants(Long userId) {
        List<FavouriteRestaurant> favouriteRestaurants = favouriteRestaurantRepository.findByUserId(userId);
        return favouriteRestaurants.stream()
                .map(this::convertToDTO)
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

    public FavouriteRestaurantDTO convertToDTO(FavouriteRestaurant favouriteRestaurant) {
        FavouriteRestaurantDTO dto = new FavouriteRestaurantDTO();

        dto.setId(favouriteRestaurant.getId());

        Restaurant restaurant = favouriteRestaurant.getRestaurant();
        if (restaurant != null) {
            dto.setRestaurantId(restaurant.getId());
            dto.setRestaurantName(restaurant.getName());
            dto.setRestaurantCategory(restaurant.getCategory());
            dto.setRestaurantLogoUrl(restaurant.getLogoUrl());


            RestaurantAddress address = restaurant.getRestaurantAddress();
            if (address != null) {
                dto.setStreet(address.getStreet());
                dto.setFlatNumber(address.getFlatNumber());
            }

            List<OpinionDTO> opinions = restaurant.getRestaurantOpinions().stream()
                    .map(restaurantOpinion -> {
                        OpinionDTO opinion = new OpinionDTO();
                        opinion.setId(restaurantOpinion.getId());
                        opinion.setQualityRating(restaurantOpinion.getQualityRating());
                        opinion.setDeliveryRating(restaurantOpinion.getDeliveryRating());
                        return opinion;
                    })
                    .collect(Collectors.toList());
            dto.setRestaurantOpinion(opinions);
        }

        return dto;

    }
}
