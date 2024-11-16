package pl.urban.backend.service;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import pl.urban.backend.dto.SearchedRestaurantDTO;
import pl.urban.backend.model.Delivery;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.model.RestaurantAddress;
import pl.urban.backend.repository.RestaurantAddressRepository;
import pl.urban.backend.repository.RestaurantRepository;


import java.util.List;


@Service
public class RestaurantAddressService {

    private final RestaurantAddressRepository restaurantAddressRepository;
    private final RestaurantRepository restaurantRepository;
    private final GeocodingService geocodingService;
    private final DeliveryService deliveryService;

    public RestaurantAddressService(RestaurantAddressRepository restaurantAddressRepository, RestaurantRepository restaurantRepository, GeocodingService geocodingService, DeliveryService deliveryService) {
        this.restaurantAddressRepository = restaurantAddressRepository;
        this.restaurantRepository = restaurantRepository;
        this.geocodingService = geocodingService;

        this.deliveryService = deliveryService;
    }

    public RestaurantAddress getRestaurantAddress(Long restaurantId) {
        return restaurantAddressRepository.findByRestaurantId(restaurantId);
    }

    public List<SearchedRestaurantDTO> searchNearbyRestaurants(String address, double radiusKm) throws JSONException {
        String formattedAddress = removeCommas(address);
        double[] coords = geocodingService.getCoordinates(formattedAddress);
        double latitude = coords[0];
        double longitude = coords[1];

        List<RestaurantAddress> restaurantAddresses = restaurantAddressRepository.findNearbyRestaurants(latitude, longitude, radiusKm);
        return restaurantAddresses.stream()
                .map(restaurantAddress -> convertToDTO(restaurantAddress, latitude, longitude))
                .toList();

    }

    private String removeCommas(String address) {
        String addressWithoutCommas = address.replace(",", "");
        return addressWithoutCommas
                .replace("ą", "a")
                .replace("ć", "c")
                .replace("ę", "e")
                .replace("ł", "l")
                .replace("ń", "n")
                .replace("ó", "o")
                .replace("ś", "s")
                .replace("ź", "z")
                .replace("ż", "z");

    }

    public SearchedRestaurantDTO convertToDTO(RestaurantAddress restaurantAddress, double userLatitude, double userLongitude) {
        SearchedRestaurantDTO dto = new SearchedRestaurantDTO();

        dto.setLatitude(restaurantAddress.getLatitude());
        dto.setLongitude(restaurantAddress.getLongitude());
        dto.setRestaurantId(restaurantAddress.getRestaurant().getId());

        double distance = calculateDistance(userLatitude, userLongitude, restaurantAddress.getLatitude(), restaurantAddress.getLongitude());
        dto.setDistance(distance);

        Delivery delivery = deliveryService.getDelivery(restaurantAddress.getRestaurant().getId());
        dto.setPickup(delivery.getPickupTime() > 0);

        Restaurant restaurant = restaurantRepository.findById(restaurantAddress.getRestaurant().getId()).orElseThrow();
        dto.setName(restaurant.getName());
        dto.setCategory(restaurant.getCategory());

        return dto;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;

        return Math.round(distance * 1000.0) / 1000.0;

    }

}