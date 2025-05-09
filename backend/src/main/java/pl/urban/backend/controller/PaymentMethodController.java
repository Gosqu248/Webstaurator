package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.dto.response.PaymentResponse;
import pl.urban.backend.service.PaymentMethodService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paymentMethods")
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;

    @GetMapping("/getRestaurantPayments")
    public List<PaymentResponse> getAllPaymentForRestaurant(@RequestParam Long restaurantId) {
        return paymentMethodService.getALlPaymentsByRestaurantId(restaurantId);
    }

    @GetMapping("/getAllPayments")
    public List<PaymentResponse> getAllPayments() {
        return paymentMethodService.getAllPayments();
    }


}
