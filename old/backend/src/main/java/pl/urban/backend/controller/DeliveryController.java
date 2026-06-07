package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.response.DeliveryHourResponse;
import pl.urban.backend.dto.response.DeliveryResponse;
import pl.urban.backend.service.DeliveryHourService;
import pl.urban.backend.service.DeliveryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryHourService deliveryHourService;
    private final DeliveryService deliveryService;

    @GetMapping
    public DeliveryResponse getDelivery(@RequestParam Long restaurantId) {
        return deliveryService.getDelivery(restaurantId);
    }

    @GetMapping("/times")
    public List<DeliveryHourResponse> getDeliveryTime(@RequestParam Long restaurantId) {
        return deliveryHourService.getDeliveryTimeFromRestaurantId(restaurantId);
    }
}
