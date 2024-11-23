package pl.urban.backend.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.urban.backend.model.DeliveryHour;
import pl.urban.backend.repository.DeliveryHourRepository;

import java.util.List;

@Service
public class DeliveryHourService {


    private final DeliveryHourRepository deliveryHourRepository;

    public DeliveryHourService(DeliveryHourRepository deliveryHourRepository) {
        this.deliveryHourRepository = deliveryHourRepository;
    }

    public List<DeliveryHour> getDeliveryTimeFromRestaurantId(Long restaurantId) {
        return deliveryHourRepository.findByRestaurantId(restaurantId, Sort.by(Sort.Direction.ASC, "dayOfWeek"));
    }


}
