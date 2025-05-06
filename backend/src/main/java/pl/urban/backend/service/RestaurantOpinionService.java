package pl.urban.backend.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.RestaurantOpinionDTO;
import pl.urban.backend.dto.UserNameResponse;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.OrderRepository;
import pl.urban.backend.repository.RestaurantOpinionRepository;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Service
public class RestaurantOpinionService {

    private final RestaurantOpinionRepository restaurantOpinionRepository;


    private final OrderRepository orderRepository;

    public RestaurantOpinionService(RestaurantOpinionRepository restaurantOpinionRepository, OrderRepository orderRepository) {
        this.restaurantOpinionRepository = restaurantOpinionRepository;

        this.orderRepository = orderRepository;
    }

    public List<RestaurantOpinionDTO> getRestaurantOpinion(Long restaurantId) {
        List<RestaurantOpinion> favouriteRestaurants = restaurantOpinionRepository.findAllByRestaurantId(restaurantId, Sort.by(Sort.Direction.DESC, "createdAt"));
        return favouriteRestaurants.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public double getRestaurantRating(Long restaurantId) {
        List<RestaurantOpinion> restaurantOpinions = restaurantOpinionRepository.findAllByRestaurantId(restaurantId);
        double avg = restaurantOpinions.stream()
                .map(opinion -> (opinion.getQualityRating() + opinion.getDeliveryRating()) / 2.0)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        BigDecimal bd = BigDecimal.valueOf(avg);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public RestaurantOpinion addOpinion(RestaurantOpinionDTO opinionDTO, Long orderId) {
        RestaurantOpinion opinion = new RestaurantOpinion();

        opinion.setQualityRating(opinionDTO.getQualityRating());
        opinion.setDeliveryRating(opinionDTO.getDeliveryRating());
        opinion.setComment(opinionDTO.getComment());

        Order order = orderRepository.findById(orderId).orElseThrow();
        User user = order.getUser();
        Restaurant restaurant = order.getRestaurant();

        opinion.setOrder(order);
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
            UserNameResponse userResponse = new UserNameResponse(user.getId(), user.getName());
            dto.setUser(userResponse);
        }

        return dto;

    }


}
