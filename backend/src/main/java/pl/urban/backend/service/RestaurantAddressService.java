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
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class RestaurantAddressService {

    private final RestaurantAddressRepository restaurantAddressRepository;
    private final GeocodingService geocodingService;
    private final DeliveryService deliveryService;

    public RestaurantAddressService(RestaurantAddressRepository restaurantAddressRepository, GeocodingService geocodingService, DeliveryService deliveryService) {
        this.restaurantAddressRepository = restaurantAddressRepository;
        this.geocodingService = geocodingService;

        this.deliveryService = deliveryService;
    }

    public RestaurantAddress getRestaurantAddress(Long restaurantId) {
        return restaurantAddressRepository.findByRestaurantId(restaurantId);
    }



    public List<SearchedRestaurantDTO> searchNearbyRestaurants(String address, double radiusKm) throws JSONException {
        double[] coords = geocodingService.getCoordinates(address);
        double latitude = coords[0];
        double longitude = coords[1];

        List<RestaurantAddress> restaurantAddresses = restaurantAddressRepository.findNearbyRestaurants(latitude, longitude, radiusKm);

        return restaurantAddresses.parallelStream()
                .map(restaurantAddress -> convertToDTO(restaurantAddress, latitude, longitude))
                .collect(Collectors.toList());

    }



    public SearchedRestaurantDTO convertToDTO(RestaurantAddress restaurantAddress, double userLatitude, double userLongitude) {
        SearchedRestaurantDTO dto = new SearchedRestaurantDTO();

        dto.setLatitude(restaurantAddress.getLatitude());
        dto.setLongitude(restaurantAddress.getLongitude());
        dto.setRestaurantId(restaurantAddress.getRestaurant().getId());

        double distance = calculateDistance(userLatitude, userLongitude, restaurantAddress.getLatitude(), restaurantAddress.getLongitude());
        dto.setDistance(distance);

        Long restaurantId = restaurantAddress.getRestaurant().getId();

        Delivery delivery = deliveryService.getDelivery(restaurantId);
        dto.setPickup(delivery != null && delivery.getPickupTime() > 0);

        Restaurant restaurant = restaurantAddress.getRestaurant();
        if (restaurant != null) {
            dto.setName(restaurant.getName());
            dto.setCategory(restaurant.getCategory());
        } else {
            throw new IllegalStateException("Restaurant details are not available for address ID: " + restaurantAddress.getId());
        }

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