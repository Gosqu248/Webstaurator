package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import pl.urban.backend.dto.response.CoordinatesResponse;
import pl.urban.backend.dto.response.RestaurantAddressResponse;
import pl.urban.backend.dto.response.SearchedRestaurantResponse;
import pl.urban.backend.model.Delivery;
import pl.urban.backend.model.RestaurantAddress;
import pl.urban.backend.repository.DeliveryRepository;
import pl.urban.backend.repository.RestaurantAddressRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RestaurantAddressService {

    private final RestaurantAddressRepository restaurantAddressRepository;
    private final GeocodingService geocodingService;
    private final DeliveryRepository deliveryRepository;
    private final RestaurantOpinionService restaurantOpinionService;
    private final MapperService mapper;

    public RestaurantAddressResponse getRestaurantAddress(Long restaurantId) {
        return mapper.fromRestaurantAddress(restaurantAddressRepository.findByRestaurantId(restaurantId));
    }

    public CoordinatesResponse getCoordinatesByRestaurantId(Long restaurantId) {
        return toCoordinatesResponse(restaurantAddressRepository.findByRestaurantId(restaurantId));
    }

//    public List<RestaurantAddress> findAll() {
//        return restaurantAddressRepository.findAll();
//    }

    public List<SearchedRestaurantResponse> searchNearbyRestaurants(String address, double radiusKm) {
        double[] coords = geocodingService.getCoordinates(address);
        double latitude = coords[0];
        double longitude = coords[1];

        List<RestaurantAddress> restaurantAddresses = searchNearbyRestaurants(latitude, longitude, radiusKm);

        return restaurantAddresses.parallelStream()
                .map(restaurantAddress -> toSearchedRestaurantResponse(restaurantAddress, latitude, longitude))
                .collect(Collectors.toList());
    }

    public SearchedRestaurantResponse toSearchedRestaurantResponse(RestaurantAddress restaurantAddress, double userLatitude, double userLongitude) {
        Long restaurantId = restaurantAddress.getRestaurant().getId();

        double distance = calculateDistance(userLatitude, userLongitude, restaurantAddress.getLatitude(), restaurantAddress.getLongitude());

        Delivery delivery = deliveryRepository.findByRestaurantId(restaurantId);
        boolean isPickupAvailable = delivery != null && delivery.getPickupTime() > 0;
        double deliveryPrice = delivery != null ? delivery.getDeliveryPrice() : 0;

        return new SearchedRestaurantResponse(
                restaurantId,
                restaurantAddress.getRestaurant().getName(),
                restaurantAddress.getRestaurant().getCategory(),
                isPickupAvailable,
                distance,
                restaurantOpinionService.getRestaurantRating(restaurantId),
                deliveryPrice,
                restaurantAddress.getLatitude(),
                restaurantAddress.getLongitude()
        );
    }

    private List<RestaurantAddress> searchNearbyRestaurants(double latitude, double longitude, double radiusKm) {
        double earthRadiusKm = 6371.0;
        double deltaLatitude = radiusKm / 111.0;
        double deltaLongitude = radiusKm / (earthRadiusKm * Math.cos(Math.toRadians(latitude)));

        double minLat = latitude - Math.toDegrees(deltaLatitude);
        double maxLat = latitude + Math.toDegrees(deltaLatitude);
        double minLon = longitude - Math.toDegrees(deltaLongitude);
        double maxLon = longitude + Math.toDegrees(deltaLongitude);

        return restaurantAddressRepository.findNearbyRestaurants(
                minLat, maxLat, minLon, maxLon
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

//    public List<Long> searchTime(String address, double radiusKm) {
//        double[] coords = geocodingService.getCoordinates(address);
//        double latitude = coords[0];
//        double longitude = coords[1];
//
//        long startTime = System.currentTimeMillis();
//
//        List<RestaurantAddress> restaurantAddresses = searchNearbyRestaurants(latitude, longitude, radiusKm);
//        long endTime = System.currentTimeMillis();
//
//        List<SearchedRestaurantResponse> dto = restaurantAddresses.parallelStream()
//                .map(restaurantAddress -> toSearchedRestaurantResponse(restaurantAddress, latitude, longitude))
//                .toList();
//
//        long endTimeMax = System.currentTimeMillis();
//
//        List<Long> times = new ArrayList<>();
//        times.add(endTime - startTime);
//        times.add(endTimeMax - endTime);
//
//        return times;
//    }

    CoordinatesResponse toCoordinatesResponse(RestaurantAddress restaurantAddress) {
        return new CoordinatesResponse(
                restaurantAddress.getLatitude(),
                restaurantAddress.getLongitude()
        );
    }

}
