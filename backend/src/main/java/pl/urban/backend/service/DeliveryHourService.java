package pl.urban.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.urban.backend.model.DeliveryHour;
import pl.urban.backend.repository.DeliveryHourRepository;

import java.util.List;

@Service
public class DeliveryHourService {

    @Autowired
    private DeliveryHourRepository deliveryHourRepository;

    public List<DeliveryHour> getDeliveryTimeFromRestaurantId(Long restaurantId) {
        return deliveryHourRepository.findByRestaurantId(restaurantId);
    }


}
