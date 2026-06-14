package com.gosqu.restaurant.hours;

import com.gosqu.restaurant.hours.dto.OpeningHoursRequest;
import com.gosqu.restaurant.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpeningHoursService {

    private final OpeningHoursRepository openingHoursRepository;
    private final RestaurantService restaurantService;

    @Transactional
    public void setOpeningHours(UUID ownerId, UUID restaurantId, OpeningHoursRequest request) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        openingHoursRepository.deleteAllByRestaurantId(restaurantId);
        List<OpeningHours> hours = request.hours().stream()
                .map(h -> OpeningHours.builder()
                        .restaurantId(restaurantId)
                        .dayOfWeek(h.dayOfWeek())
                        .openTime(h.openTime())
                        .closeTime(h.closeTime())
                        .build())
                .toList();
        openingHoursRepository.saveAll(hours);
        log.info("opening_hours_updated restaurantId={} count={}", restaurantId, hours.size());
    }
}
