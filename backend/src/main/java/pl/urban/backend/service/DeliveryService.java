package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.response.DeliveryResponse;
import pl.urban.backend.repository.DeliveryRepository;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final MapperService mapper;
    public DeliveryResponse getDelivery(Long restaurantId) {
        return mapper.fromDelivery(deliveryRepository.findByRestaurantId(restaurantId));
    }
}
