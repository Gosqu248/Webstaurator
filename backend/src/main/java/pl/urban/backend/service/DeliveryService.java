package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.urban.backend.model.Delivery;
import pl.urban.backend.repository.DeliveryRepository;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public Delivery getDelivery(Long restaurantId) {
        return deliveryRepository.findByRestaurantId(restaurantId);
    }
}
