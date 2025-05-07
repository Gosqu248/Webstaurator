package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.urban.backend.dto.request.RestaurantOpinionRequest;
import pl.urban.backend.dto.response.RestaurantOpinionResponse;
import pl.urban.backend.dto.response.UserNameResponse;
import pl.urban.backend.model.Order;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.model.RestaurantOpinion;
import pl.urban.backend.model.User;
import pl.urban.backend.repository.OrderRepository;
import pl.urban.backend.repository.RestaurantOpinionRepository;
import pl.urban.backend.service.RestaurantOpinionService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantOpinionTests {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantOpinionTests.class);

    private final RestaurantOpinionRepository restaurantOpinionRepository = mock(RestaurantOpinionRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final RestaurantOpinionService service = new RestaurantOpinionService(restaurantOpinionRepository, orderRepository);

    @Test
    void getRestaurantOpinion_ShouldReturnListOfOpinions() {
        Long restaurantId = 1L;
        RestaurantOpinion opinion = new RestaurantOpinion();
        opinion.setId(1L);

        when(restaurantOpinionRepository.findAllByRestaurantId(eq(restaurantId), any()))
                .thenReturn(List.of(opinion));

        List<RestaurantOpinionResponse> result = service.getRestaurantOpinion(restaurantId);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().id());
        logger.info("Test zakończony sukcesem - znaleziono {} opinii dla restauracji o ID: {}", result.size(), restaurantId);
    }

    @Test
    void getRestaurantRating_ShouldReturnAverageRating() {
        Long restaurantId = 1L;
        RestaurantOpinion opinion1 = new RestaurantOpinion();
        opinion1.setQualityRating(4);
        opinion1.setDeliveryRating(5);

        RestaurantOpinion opinion2 = new RestaurantOpinion();
        opinion2.setQualityRating(3);
        opinion2.setDeliveryRating(4);

        when(restaurantOpinionRepository.findAllByRestaurantId(restaurantId))
                .thenReturn(List.of(opinion1, opinion2));

        double result = service.getRestaurantRating(restaurantId);

        assertEquals(4.0, result, 0.1);
        logger.info("Test zakończony sukcesem - średnia ocena dla restauracji o ID: {} wynosi: {}", restaurantId, result);
    }

    @Test
    void addOpinion_ShouldSaveAndReturnOpinion() {
        Long orderId = 1L;
        UserNameResponse userNameResponse = new UserNameResponse(1L, "John");
        RestaurantOpinionRequest dto = new RestaurantOpinionRequest(
                1L,
                5,
                "Good",
                userNameResponse
        );

        Order order = new Order();
        User user = new User();
        user.setId(1L);
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        order.setUser(user);
        order.setRestaurant(restaurant);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        RestaurantOpinion savedOpinion = new RestaurantOpinion();
        savedOpinion.setId(1L);

        when(restaurantOpinionRepository.save(any(RestaurantOpinion.class))).thenReturn(savedOpinion);

        RestaurantOpinion result = service.addOpinion(dto, orderId);

        assertEquals(1L, result.getId());
        verify(restaurantOpinionRepository).save(any(RestaurantOpinion.class));
        logger.info("Test zakończony sukcesem - dodano opinię o ID: {} dla zamówienia o ID: {}", result.getId(), orderId);
    }
}
