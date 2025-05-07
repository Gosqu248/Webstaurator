package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.urban.backend.dto.request.AddRestaurantRequest;
import pl.urban.backend.dto.response.RestaurantResponse;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.PaymentRepository;
import pl.urban.backend.repository.RestaurantRepository;
import pl.urban.backend.service.DeliveryHourService;
import pl.urban.backend.service.GeocodingService;
import pl.urban.backend.service.RestaurantService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.urban.backend.repository.RestaurantAddressRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class RestaurantTests {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantTests.class);

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private GeocodingService geocodingService;

    private Restaurant testRestaurant;

    @Mock
    private DeliveryHourService deliveryHourService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestaurantAddressRepository restaurantAddressRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantAddressRepository = mock(RestaurantAddressRepository.class);

        RestaurantAddress testRestaurantAddress = new RestaurantAddress();
        testRestaurantAddress.setCity("Zakliczyn");
        testRestaurantAddress.setStreet("Spokojna");
        testRestaurantAddress.setFlatNumber("10");
        testRestaurantAddress.setZipCode("32-840");
        testRestaurantAddress.setLatitude(49.85496906411742);
        testRestaurantAddress.setLongitude(20.804921425415863);


        AddRestaurantRequest addRestaurantRequest = new AddRestaurantRequest(
                "Test Restaurant",
                "Fast Food",
                "http://example.com/logo.png",
                "http://example.com/image.png",
                testRestaurantAddress,
                List.of("PayU"),
                new Delivery(),
                List.of(),
                new HashSet<>()
        );
        testRestaurant = new Restaurant();
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setId(1L);
        testRestaurant.setRestaurantAddress(testRestaurantAddress);
    }

    @Test
    void testAddRestaurant() {
        RestaurantAddress address = new RestaurantAddress();
        address.setStreet("Main Street");
        address.setFlatNumber("10");
        address.setZipCode("12345");
        address.setCity("Sample City");

        Delivery delivery = new Delivery();
        delivery.setDeliveryMinTime(30);
        delivery.setDeliveryMaxTime(60);
        delivery.setDeliveryPrice(5.99);
        delivery.setMinimumPrice(20.0);
        delivery.setPickupTime(15);


        Menu newMenu = new Menu();
        newMenu.setName("Burgerowy król");
        newMenu.setCategory("Burger");
        newMenu.setPrice(15.0);

        Set<Menu> menus = new HashSet<>();
        menus.add(newMenu);


        List<DeliveryHour> deliveryHour = deliveryHourService.getDeliveryTimeFromRestaurantId(1L);


        AddRestaurantRequest dto = new AddRestaurantRequest(
                "Test Restaurant",
                "Fast Food",
                "http://example.com/logo.png",
                "http://example.com/image.png",
                address,
                List.of("PayU"),
                delivery,
                deliveryHour,
                menus
        );

        when(geocodingService.getCoordinates(anyString())).thenReturn(new double[]{52.2297, 21.0122});
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(new Restaurant());

        RestaurantResponse result = restaurantService.addRestaurant(dto);

        assertNotNull(result);
        verify(geocodingService).getCoordinates("Main Street 10, 12345 Sample City");
    }



    @Test
    void testRemoveRestaurant() {
        logger.info("Running testRemoveRestaurant");
        when(restaurantRepository.findById(anyLong())).thenReturn(java.util.Optional.of(testRestaurant));

        restaurantService.removeRestaurant(1L);

        verify(restaurantRepository, times(1)).delete(any(Restaurant.class));
        logger.info("Completed testRemoveRestaurant");
    }

    @Test
    void testGetRestaurantById() {
        logger.info("Running testGetRestaurantById");
        when(restaurantRepository.findById(anyLong())).thenReturn(java.util.Optional.of(testRestaurant));

        RestaurantResponse result = restaurantService.getRestaurantById(1L);

        assertNotNull(result);
        assertEquals("Test Restaurant", result.name());
        logger.info("Completed testGetRestaurantById");
    }

    public RestaurantAddressRepository getRestaurantAddressRepository() {
        return restaurantAddressRepository;
    }

    public void setRestaurantAddressRepository(RestaurantAddressRepository restaurantAddressRepository) {
        this.restaurantAddressRepository = restaurantAddressRepository;
    }
}
