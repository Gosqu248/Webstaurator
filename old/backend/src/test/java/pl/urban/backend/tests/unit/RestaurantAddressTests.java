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
import pl.urban.backend.dto.response.CoordinatesResponse;
import pl.urban.backend.dto.response.RestaurantAddressResponse;
import pl.urban.backend.dto.response.SearchedRestaurantResponse;
import pl.urban.backend.model.Restaurant;
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

    private RestaurantAddressResponse testRestaurantAddress;
    private SearchedRestaurantResponse testSearchedRestaurantResponse;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test environment");
        MockitoAnnotations.openMocks(this);

        // Initialize test restaurant address
        Restaurant testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setCategory("Italian");

        testRestaurantAddress = new RestaurantAddressResponse(
                1L,
                "Test Street 123",
                "Test City",
                "Test Country",
                "33-100",
                52.229676,
                21.012229,
                testRestaurant.getId()
        );
        // Initialize test DTO
        testSearchedRestaurantResponse = new SearchedRestaurantResponse(
                1L,
                "Test Restaurant",
                "Italian",
                true,
                10.0,
                4.5,
                2.5,
                52.229676,
                21.012229
        );

        logger.info("Test environment setup completed");
    }

    @Test
    void testSearchRestaurants() {
        logger.info("Running testSearchRestaurants");

        when(restaurantAddressService.searchNearbyRestaurants(anyString(), anyDouble()))
                .thenReturn(Collections.singletonList(testSearchedRestaurantResponse));

        ResponseEntity<List<SearchedRestaurantResponse>> response = restaurantAddressController
                .searchRestaurants("Test Street 123", 6.0);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        SearchedRestaurantResponse result = response.getBody().getFirst();
        assertEquals(testSearchedRestaurantResponse.restaurantId(), result.restaurantId());
        assertEquals(testSearchedRestaurantResponse.name(), result.name());
        assertEquals(testSearchedRestaurantResponse.category(), result.category());
        assertEquals(testSearchedRestaurantResponse.distance(), result.distance());
        assertEquals(testSearchedRestaurantResponse.rating(), result.rating());

        logger.info("Completed testSearchRestaurants");
    }

    @Test
    void testSearchRestaurantsWithDefaultRadius() {
        logger.info("Running testSearchRestaurantsWithDefaultRadius");

        when(restaurantAddressService.searchNearbyRestaurants(anyString(), eq(6.0)))
                .thenReturn(Collections.singletonList(testSearchedRestaurantResponse));

        ResponseEntity<List<SearchedRestaurantResponse>> response = restaurantAddressController
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

        RestaurantAddressResponse result = restaurantAddressController.getRestaurantAddress(1L);

        assertNotNull(result);
        assertEquals(testRestaurantAddress.restaurantId(), result.restaurantId());
        assertEquals(testRestaurantAddress.latitude(), result.latitude());
        assertEquals(testRestaurantAddress.longitude(), result.longitude());

        logger.info("Completed testGetRestaurantAddress");
    }

    @Test
    void testGetCoordinates() {
        logger.info("Running testGetCoordinates");

        CoordinatesResponse testCoordinates = new CoordinatesResponse(
                52.229676,
                21.012229
        );

        when(restaurantAddressService.getCoordinatesByRestaurantId(1L))
                .thenReturn(testCoordinates);

        ResponseEntity<CoordinatesResponse> response = restaurantAddressController.getCoordinates(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testCoordinates.lat(), response.getBody().lat());
        assertEquals(testCoordinates.lon(), response.getBody().lon());

        logger.info("Completed testGetCoordinates");
    }

    @Test
    void testGetRestaurantAddressNotFound() {
        logger.info("Running testGetRestaurantAddressNotFound");

        when(restaurantAddressService.getRestaurantAddress(999L))
                .thenReturn(null);

        RestaurantAddressResponse result = restaurantAddressController.getRestaurantAddress(999L);

        assertNull(result);

        logger.info("Completed testGetRestaurantAddressNotFound");
    }
}
