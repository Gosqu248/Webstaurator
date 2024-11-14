package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.dto.FavouriteRestaurantDTO;
import pl.urban.backend.dto.RestaurantOpinionDTO;
import pl.urban.backend.dto.UserNameDTO;
import pl.urban.backend.model.FavouriteRestaurant;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.model.RestaurantOpinion;
import pl.urban.backend.model.User;
import pl.urban.backend.repository.RestaurantOpinionRepository;
import pl.urban.backend.repository.RestaurantRepository;
import pl.urban.backend.repository.UserRepository;

import java.util.List;

@Service
public class RestaurantOpinionService {

    private final RestaurantOpinionRepository restaurantOpinionRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public RestaurantOpinionService(RestaurantOpinionRepository restaurantOpinionRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.restaurantOpinionRepository = restaurantOpinionRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<RestaurantOpinionDTO> getRestaurantOpinion(Long restaurantId) {
        List<RestaurantOpinion> favouriteRestaurants = restaurantOpinionRepository.findAllByRestaurantId(restaurantId);
        return favouriteRestaurants.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public RestaurantOpinion addOpinion(RestaurantOpinionDTO opinionDTO, Long restaurantId, Long userId) {
        RestaurantOpinion opinion = new RestaurantOpinion();

        opinion.setQualityRating(opinionDTO.getQualityRating());
        opinion.setDeliveryRating(opinionDTO.getDeliveryRating());
        opinion.setComment(opinionDTO.getComment());

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        opinion.setRestaurant(restaurant);
        opinion.setUser(user);

        return restaurantOpinionRepository.save(opinion);

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
