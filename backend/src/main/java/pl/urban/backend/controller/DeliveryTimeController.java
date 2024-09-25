package pl.urban.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.DeliveryHour;
import pl.urban.backend.service.DeliveryHourService;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class DeliveryTimeController {

    @Autowired
    private DeliveryHourService deliveryHourService;

    @GetMapping("/time")
    public List<DeliveryHour> getDeliveryTime(@RequestParam Long restaurantId) {
        return deliveryHourService.getDeliveryTimeFromRestaurantId(restaurantId);
    }
}
