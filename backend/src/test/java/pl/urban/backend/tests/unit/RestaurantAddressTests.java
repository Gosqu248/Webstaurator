package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import pl.urban.backend.controller.RestaurantAddressController;
import pl.urban.backend.dto.CoordinatesDTO;
import pl.urban.backend.dto.SearchedRestaurantDTO;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.model.RestaurantAddress;
import pl.urban.backend.service.RestaurantAddressService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class RestaurantAddressTests {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantAddressTests.class);

    @InjectMocks
    private RestaurantAddressController restaurantAddressController;

    @Mock
    private RestaurantAddressService restaurantAddressService;

    private RestaurantAddress testRestaurantAddress;
    private SearchedRestaurantDTO testSearchedRestaurantDTO;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test environment");
        MockitoAnnotations.openMocks(this);

        // Initialize test restaurant address
        Restaurant testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setCategory("Italian");

        testRestaurantAddress = new RestaurantAddress();
        testRestaurantAddress.setRestaurant(testRestaurant);
        testRestaurantAddress.setLatitude(52.229676);
        testRestaurantAddress.setLongitude(21.012229);

        // Initialize test DTO
        testSearchedRestaurantDTO = new SearchedRestaurantDTO();
        testSearchedRestaurantDTO.setRestaurantId(1L);
        testSearchedRestaurantDTO.setName("Test Restaurant");
        testSearchedRestaurantDTO.setCategory("Italian");
        testSearchedRestaurantDTO.setLat(52.229676);
        testSearchedRestaurantDTO.setLon(21.012229);
        testSearchedRestaurantDTO.setDistance(2.5);
        testSearchedRestaurantDTO.setRating(4.5);
        testSearchedRestaurantDTO.setPickup(true);
        testSearchedRestaurantDTO.setDeliveryPrice(10.0);

        logger.info("Test environment setup completed");
    }

    @Test
    void testSearchRestaurants() {
        logger.info("Running testSearchRestaurants");

        when(restaurantAddressService.searchNearbyRestaurants(anyString(), anyDouble()))
                .thenReturn(Collections.singletonList(testSearchedRestaurantDTO));

        ResponseEntity<List<SearchedRestaurantDTO>> response = restaurantAddressController
                .searchRestaurants("Test Street 123", 6.0);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        SearchedRestaurantDTO result = response.getBody().getFirst();
        assertEquals(testSearchedRestaurantDTO.getRestaurantId(), result.getRestaurantId());
        assertEquals(testSearchedRestaurantDTO.getName(), result.getName());
        assertEquals(testSearchedRestaurantDTO.getCategory(), result.getCategory());
        assertEquals(testSearchedRestaurantDTO.getDistance(), result.getDistance());
        assertEquals(testSearchedRestaurantDTO.getRating(), result.getRating());

        logger.info("Completed testSearchRestaurants");
    }

    @Test
    void testSearchRestaurantsWithDefaultRadius() {
        logger.info("Running testSearchRestaurantsWithDefaultRadius");

        when(restaurantAddressService.searchNearbyRestaurants(anyString(), eq(6.0)))
                .thenReturn(Collections.singletonList(testSearchedRestaurantDTO));

        ResponseEntity<List<SearchedRestaurantDTO>> response = restaurantAddressController
                .searchRestaurants("Test Street 123", 6.0);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        logger.info("Completed testSearchRestaurantsWithDefaultRadius");
    }

    @Test
    void testGetRestaurantAddress() {
        logger.info("Running testGetRestaurantAddress");

        when(restaurantAddressService.getRestaurantAddress(1L))
                .thenReturn(testRestaurantAddress);

        RestaurantAddress result = restaurantAddressController.getRestaurantAddress(1L);

        assertNotNull(result);
        assertEquals(testRestaurantAddress.getRestaurant().getId(), result.getRestaurant().getId());
        assertEquals(testRestaurantAddress.getLatitude(), result.getLatitude());
        assertEquals(testRestaurantAddress.getLongitude(), result.getLongitude());

        logger.info("Completed testGetRestaurantAddress");
    }

    @Test
    void testGetCoordinates() {
        logger.info("Running testGetCoordinates");

        CoordinatesDTO testCoordinates = new CoordinatesDTO();
        testCoordinates.setLat(52.229676);
        testCoordinates.setLon(21.012229);

        when(restaurantAddressService.getCoordinatesByRestaurantId(1L))
                .thenReturn(testCoordinates);

        ResponseEntity<CoordinatesDTO> response = restaurantAddressController.getCoordinates(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testCoordinates.getLat(), response.getBody().getLat());
        assertEquals(testCoordinates.getLon(), response.getBody().getLon());

        logger.info("Completed testGetCoordinates");
    }

    @Test
    void testGetRestaurantAddressNotFound() {
        logger.info("Running testGetRestaurantAddressNotFound");

        when(restaurantAddressService.getRestaurantAddress(999L))
                .thenReturn(null);

        RestaurantAddress result = restaurantAddressController.getRestaurantAddress(999L);

        assertNull(result);

        logger.info("Completed testGetRestaurantAddressNotFound");
    }
}
