package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.model.Delivery;
import pl.urban.backend.repository.DeliveryRepository;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public Delivery getDelivery(Long restaurantId) {
        return deliveryRepository.findByRestaurantId(restaurantId);
    }
}
