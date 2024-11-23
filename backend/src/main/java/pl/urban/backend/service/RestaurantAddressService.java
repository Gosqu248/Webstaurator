package pl.urban.backend.service;

import org.springframework.stereotype.Service;

import pl.urban.backend.dto.CoordinatesDTO;
import pl.urban.backend.dto.SearchedRestaurantDTO;
import pl.urban.backend.model.Delivery;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.model.RestaurantAddress;
import pl.urban.backend.repository.DeliveryRepository;
import pl.urban.backend.repository.RestaurantAddressRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RestaurantAddressService {

    private final RestaurantAddressRepository restaurantAddressRepository;
    private final GeocodingService geocodingService;
    private final DeliveryRepository deliveryRepository;
    private final RestaurantOpinionService restaurantOpinionService;

    public RestaurantAddressService(RestaurantAddressRepository restaurantAddressRepository, GeocodingService geocodingService, DeliveryRepository deliveryRepository, RestaurantOpinionService restaurantOpinionService) {
        this.restaurantAddressRepository = restaurantAddressRepository;
        this.geocodingService = geocodingService;
        this.deliveryRepository = deliveryRepository;
        this.restaurantOpinionService = restaurantOpinionService;
    }

    public RestaurantAddress getRestaurantAddress(Long restaurantId) {
        return restaurantAddressRepository.findByRestaurantId(restaurantId);
    }

    public CoordinatesDTO getCoordinatesByRestaurantId(Long restaurantId) {
        RestaurantAddress restaurantAddress = restaurantAddressRepository.findByRestaurantId(restaurantId);
        return convertToCoordinatesDTO(restaurantAddress);
    }



    public List<SearchedRestaurantDTO> searchNearbyRestaurants(String address, double radiusKm) {
        double[] coords = geocodingService.getCoordinates(address);
        double latitude = coords[0];
        double longitude = coords[1];

        List<RestaurantAddress> restaurantAddresses = searchNearbyRestaurants(latitude, longitude, radiusKm);

        return restaurantAddresses.parallelStream()
                .map(restaurantAddress -> convertToDTO(restaurantAddress, latitude, longitude))
                .collect(Collectors.toList());

    }


    public SearchedRestaurantDTO convertToDTO(RestaurantAddress restaurantAddress, double userLatitude, double userLongitude) {
        SearchedRestaurantDTO dto = new SearchedRestaurantDTO();

        dto.setRestaurantId(restaurantAddress.getRestaurant().getId());
        dto.setLat(restaurantAddress.getLatitude());
        dto.setLon(restaurantAddress.getLongitude());

        Restaurant restaurant = restaurantAddress.getRestaurant();
        dto.setName(restaurant.getName());
        dto.setCategory(restaurant.getCategory());

        Long restaurantId = restaurantAddress.getRestaurant().getId();

        double distance = calculateDistance(userLatitude, userLongitude, restaurantAddress.getLatitude(), restaurantAddress.getLongitude());
        dto.setDistance(distance);

        dto.setRating(restaurantOpinionService.getRestaurantRating(restaurantId));

        Delivery delivery = deliveryRepository.findByRestaurantId(restaurantId);
        dto.setPickup(delivery != null && delivery.getPickupTime() > 0);
        dto.setDeliveryPrice(delivery != null ? delivery.getDeliveryPrice() : 0);

        return dto;
    }

    private List<RestaurantAddress> searchNearbyRestaurants(double latitude, double longitude, double radiusKm) {
        double earthRadiusKm = 6371.0;
        double deltaLatitude = radiusKm / earthRadiusKm;
        double deltaLongitude = radiusKm / (earthRadiusKm * Math.cos(Math.toRadians(latitude)));

        double minLat = latitude - Math.toDegrees(deltaLatitude);
        double maxLat = latitude + Math.toDegrees(deltaLatitude);
        double minLon = longitude - Math.toDegrees(deltaLongitude);
        double maxLon = longitude + Math.toDegrees(deltaLongitude);

        return restaurantAddressRepository.findNearbyRestaurants(
                latitude, longitude, radiusKm, minLat, maxLat, minLon, maxLon
        );
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

    public List<Long> searchTime(String address, double radiusKm) {
        double[] coords = geocodingService.getCoordinates(address);
        double latitude = coords[0];
        double longitude = coords[1];

        long startTime = System.currentTimeMillis();

        List<RestaurantAddress> restaurantAddresses = searchNearbyRestaurants(latitude, longitude, radiusKm);
        long endTime = System.currentTimeMillis();

        List<SearchedRestaurantDTO> dto = restaurantAddresses.parallelStream()
                .map(restaurantAddress -> convertToDTO(restaurantAddress, latitude, longitude))
                .toList();

        long endTimeMax = System.currentTimeMillis();

        List<Long> times = new ArrayList<>();
        times.add(endTime - startTime);
        times.add(endTimeMax - endTime);

        return times;
    }

    CoordinatesDTO convertToCoordinatesDTO(RestaurantAddress restaurantAddress) {
        CoordinatesDTO dto = new CoordinatesDTO();

        dto.setLat(restaurantAddress.getLatitude());
        dto.setLon(restaurantAddress.getLongitude());

        return dto;
    }

}