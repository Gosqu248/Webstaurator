package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.urban.backend.model.DeliveryHour;
import pl.urban.backend.repository.DeliveryHourRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryHourService {
    private final DeliveryHourRepository deliveryHourRepository;

    public List<DeliveryHour> getDeliveryTimeFromRestaurantId(Long restaurantId) {
        return deliveryHourRepository.findByRestaurantId(restaurantId, Sort.by(Sort.Direction.ASC, "dayOfWeek"));
    }


}
