package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Delivery;
import pl.urban.backend.model.DeliveryHour;
import pl.urban.backend.service.DeliveryHourService;
import pl.urban.backend.service.DeliveryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery")
public class DeliveryController {
    private final DeliveryHourService deliveryHourService;
    private final DeliveryService deliveryService;

    @GetMapping("")
    public Delivery getDelivery(@RequestParam Long restaurantId) {
        return deliveryService.getDelivery(restaurantId);
    }


    @GetMapping("/time")
    public List<DeliveryHour> getDeliveryTime(@RequestParam Long restaurantId) {
        return deliveryHourService.getDeliveryTimeFromRestaurantId(restaurantId);
    }
}
