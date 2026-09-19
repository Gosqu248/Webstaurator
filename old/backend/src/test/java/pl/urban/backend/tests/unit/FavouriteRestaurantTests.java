package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.FavouriteRestaurantRepository;
import pl.urban.backend.repository.RestaurantRepository;
import pl.urban.backend.repository.UserRepository;
import pl.urban.backend.service.FavouriteRestaurantService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavouriteRestaurantTests {

    private static final Logger logger = LoggerFactory.getLogger(FavouriteRestaurantTests.class);

    @Mock
    private FavouriteRestaurantRepository favouriteRestaurantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private FavouriteRestaurantService favouriteRestaurantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFavouriteRestaurant_ShouldAddRestaurantToFavourites() {
        logger.info("Starting test: addFavouriteRestaurant_ShouldAddRestaurantToFavourites");
        Long userId = 1L;
        Long restaurantId = 2L;

        User user = new User();
        user.setId(userId);
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        favouriteRestaurantService.addFavouriteRestaurant(userId, restaurantId);

        verify(favouriteRestaurantRepository, times(1)).save(any(FavouriteRestaurant.class));
        logger.info("Completed test: addFavouriteRestaurant_ShouldAddRestaurantToFavourites");
    }

    @Test
    void deleteFavouriteRestaurant_ShouldDeleteRestaurantFromFavourites() {
        logger.info("Starting test: deleteFavouriteRestaurant_ShouldDeleteRestaurantFromFavourites");
        Long userId = 1L;
        Long restaurantId = 2L;

        FavouriteRestaurant favouriteRestaurant = new FavouriteRestaurant();
        when(favouriteRestaurantRepository.findFirstByUserIdAndRestaurantId(userId, restaurantId))
                .thenReturn(Optional.of(favouriteRestaurant));

        favouriteRestaurantService.deleteFavouriteRestaurant(userId, restaurantId);

        verify(favouriteRestaurantRepository, times(1)).delete(favouriteRestaurant);
        logger.info("Completed test: deleteFavouriteRestaurant_ShouldDeleteRestaurantFromFavourites");
    }

    @Test
    void deleteFavouriteRestaurant_ShouldThrowException_WhenFavouriteNotFound() {
        logger.info("Starting test: deleteFavouriteRestaurant_ShouldThrowException_WhenFavouriteNotFound");
        Long userId = 1L;
        Long restaurantId = 2L;

        when(favouriteRestaurantRepository.findFirstByUserIdAndRestaurantId(userId, restaurantId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                favouriteRestaurantService.deleteFavouriteRestaurant(userId, restaurantId));
        logger.info("Completed test: deleteFavouriteRestaurant_ShouldThrowException_WhenFavouriteNotFound");
    }

    @Test
    void isFavouriteRestaurant_ShouldReturnTrueIfFavouriteExists() {
        logger.info("Starting test: isFavouriteRestaurant_ShouldReturnTrueIfFavouriteExists");
        Long userId = 1L;
        Long restaurantId = 2L;

        when(favouriteRestaurantRepository.existsByUserIdAndRestaurantId(userId, restaurantId)).thenReturn(true);

        boolean result = favouriteRestaurantService.isFavouriteRestaurant(userId, restaurantId);

        assertTrue(result);
        logger.info("Completed test: isFavouriteRestaurant_ShouldReturnTrueIfFavouriteExists");
    }

    @Test
    void isFavouriteRestaurant_ShouldReturnFalseIfFavouriteDoesNotExist() {
        logger.info("Starting test: isFavouriteRestaurant_ShouldReturnFalseIfFavouriteDoesNotExist");
        Long userId = 1L;
        Long restaurantId = 2L;

        when(favouriteRestaurantRepository.existsByUserIdAndRestaurantId(userId, restaurantId)).thenReturn(false);

        boolean result = favouriteRestaurantService.isFavouriteRestaurant(userId, restaurantId);

        assertFalse(result);
        logger.info("Completed test: isFavouriteRestaurant_ShouldReturnFalseIfFavouriteDoesNotExist");
    }

}
