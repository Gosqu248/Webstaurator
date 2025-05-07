package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.request.RestaurantOpinionRequest;
import pl.urban.backend.dto.response.RestaurantOpinionResponse;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.OrderRepository;
import pl.urban.backend.repository.RestaurantOpinionRepository;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RestaurantOpinionService {

    private final RestaurantOpinionRepository restaurantOpinionRepository;
    private final OrderRepository orderRepository;
    private final MapperService mapper;

    public List<RestaurantOpinionResponse> getRestaurantOpinion(Long restaurantId) {
        List<RestaurantOpinion> favouriteRestaurants = restaurantOpinionRepository.findAllByRestaurantId(restaurantId, Sort.by(Sort.Direction.DESC, "createdAt"));
        return favouriteRestaurants.stream()
                .map(mapper::fromRestaurantOpinion)
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

    public RestaurantOpinionResponse addOpinion(RestaurantOpinionRequest opinionRequest, Long orderId) {
        RestaurantOpinion opinion = new RestaurantOpinion();

        opinion.setQualityRating(opinionRequest.qualityRating());
        opinion.setDeliveryRating(opinionRequest.deliveryRating());
        opinion.setComment(opinionRequest.comment());

        Order order = orderRepository.findById(orderId).orElseThrow();
        User user = order.getUser();
        Restaurant restaurant = order.getRestaurant();

        opinion.setOrder(order);
        opinion.setRestaurant(restaurant);
        opinion.setUser(user);

        return mapper.fromRestaurantOpinion(restaurantOpinionRepository.save(opinion));

    }
}
