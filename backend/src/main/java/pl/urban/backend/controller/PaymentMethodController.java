package pl.urban.backend.controller;

import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Payment;
import pl.urban.backend.service.PaymentMethodService;

import java.util.List;

@RestController
@RequestMapping("/api/paymentMethods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping("/getAll")
    public List<Payment> getAllPayments() {
        return paymentMethodService.getAllPayments();
    }

    @GetMapping("/getRestaurantPayments")
    public List<Payment> getAllPaymentForRestaurant(@RequestParam Long restaurantId) {
        return paymentMethodService.getALlPaymentsByRestaurantId(restaurantId);
    }






}