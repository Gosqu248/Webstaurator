package pl.urban.backend.controller;

import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Payment;
import pl.urban.backend.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/getAll")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/getRestaurantPayments")
    public List<Payment> getAllPaymentForRestaurant(@RequestParam Long restaurantId) {
        return paymentService.getALlPaymentsByRestaurantId(restaurantId);
    }
}
