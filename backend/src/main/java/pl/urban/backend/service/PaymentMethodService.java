package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.model.Payment;
import pl.urban.backend.repository.PaymentRepository;

import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentRepository paymentRepository;

    public PaymentMethodService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getALlPaymentsByRestaurantId(Long restaurantId) {
        if (restaurantId <= 0) {
            throw new IllegalArgumentException("Invalid restaurant ID");
        }
        List<Payment> payments = paymentRepository.findByRestaurantsId(restaurantId);
        if (payments.isEmpty()) {
            throw new IllegalArgumentException("No payments found for the given restaurant ID");
        }
        return payments;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
