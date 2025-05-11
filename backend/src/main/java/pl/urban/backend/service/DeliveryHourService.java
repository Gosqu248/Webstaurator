package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.response.DeliveryHourResponse;
import pl.urban.backend.repository.DeliveryHourRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryHourService {
    private final DeliveryHourRepository deliveryHourRepository;
    private final MapperService mapper;

    public List<DeliveryHourResponse> getDeliveryTimeFromRestaurantId(Long restaurantId) {
        return deliveryHourRepository.findByRestaurantId(
                restaurantId, Sort.by(Sort.Direction.ASC, "dayOfWeek"))
                .stream()
                .map(mapper::fromDeliveryHour)
                .collect(Collectors.toList());
    }
}
