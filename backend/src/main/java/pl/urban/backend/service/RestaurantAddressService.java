package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.model.RestaurantAddress;
import pl.urban.backend.repository.RestaurantAddressRepository;

@Service
public class RestaurantAddressService {

    private final RestaurantAddressRepository restaurantAddressRepository;

    public RestaurantAddressService(RestaurantAddressRepository restaurantAddressRepository) {
        this.restaurantAddressRepository = restaurantAddressRepository;
    }

    public RestaurantAddress getRestaurantAddress(Long restaurantId) {
        return restaurantAddressRepository.findByRestaurantId(restaurantId);
    }
}
