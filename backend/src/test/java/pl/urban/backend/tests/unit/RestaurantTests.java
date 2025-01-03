package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.urban.backend.dto.AddRestaurantDTO;
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

    private AddRestaurantDTO addRestaurantDTO;
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


        addRestaurantDTO = new AddRestaurantDTO();
        addRestaurantDTO.setName("Test Restaurant");
        addRestaurantDTO.setRestaurantAddress(testRestaurantAddress);

        testRestaurant = new Restaurant();
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setId(1L);
        testRestaurant.setRestaurantAddress(testRestaurantAddress);
    }

    @Test
    void testAddRestaurant() {
        AddRestaurantDTO dto = new AddRestaurantDTO();
        dto.setName("Test Restaurant");
        dto.setCategory("Fast Food");
        dto.setLogoUrl("http://example.com/logo.png");
        dto.setImageUrl("http://example.com/image.png");

        RestaurantAddress address = new RestaurantAddress();
        address.setStreet("Main Street");
        address.setFlatNumber("10");
        address.setZipCode("12345");
        address.setCity("Sample City");
        dto.setRestaurantAddress(address);

        Delivery delivery = new Delivery();
        delivery.setDeliveryMinTime(30);
        delivery.setDeliveryMaxTime(60);
        delivery.setDeliveryPrice(5.99);
        delivery.setMinimumPrice(20.0);
        delivery.setPickupTime(15);
        dto.setDelivery(delivery);

        Menu newMenu = new Menu();
        newMenu.setName("Burgerowy król");
        newMenu.setCategory("Burger");
        newMenu.setPrice(15.0);

        Set<Menu> menus = new HashSet<>();
        menus.add(newMenu);
        dto.setMenu(menus);

        List<DeliveryHour> deliveryHour = deliveryHourService.getDeliveryTimeFromRestaurantId(1L);
        dto.setDeliveryHours(deliveryHour);

        dto.setPaymentMethods(List.of("PayU"));


        when(geocodingService.getCoordinates(anyString())).thenReturn(new double[]{52.2297, 21.0122});
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(new Restaurant());

        Restaurant result = restaurantService.addRestaurant(dto);

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

        Restaurant result = restaurantService.getRestaurantById(1L);

        assertNotNull(result);
        assertEquals("Test Restaurant", result.getName());
        logger.info("Completed testGetRestaurantById");
    }

}
