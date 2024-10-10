package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.dto.FavouriteRestaurantDTO;
import pl.urban.backend.dto.RestaurantOpinionDTO;
import pl.urban.backend.dto.UserNameDTO;
import pl.urban.backend.model.FavouriteRestaurant;
import pl.urban.backend.model.RestaurantOpinion;
import pl.urban.backend.model.User;
import pl.urban.backend.repository.RestaurantOpinionRepository;

import java.util.List;

@Service
public class RestaurantOpinionService {

    private final RestaurantOpinionRepository restaurantOpinionRepository;

    public RestaurantOpinionService(RestaurantOpinionRepository restaurantOpinionRepository) {
        this.restaurantOpinionRepository = restaurantOpinionRepository;
    }

    public List<RestaurantOpinionDTO> getRestaurantOpinion(Long restaurantId) {
        List<RestaurantOpinion> favouriteRestaurants = restaurantOpinionRepository.findAllByRestaurantId(restaurantId);
        return favouriteRestaurants.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }


    public RestaurantOpinionDTO convertToDTO(RestaurantOpinion opinion) {
        RestaurantOpinionDTO dto = new RestaurantOpinionDTO();

        dto.setId(opinion.getId());
        dto.setQualityRating(opinion.getQualityRating());
        dto.setDeliveryRating(opinion.getDeliveryRating());
        dto.setComment(opinion.getComment());
        dto.setCreatedAt(opinion.getCreatedAt());

        User user = opinion.getUser();
        if (user != null) {
            UserNameDTO userDTO = new UserNameDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            dto.setUser(userDTO);
        }

        return dto;

    }


}
