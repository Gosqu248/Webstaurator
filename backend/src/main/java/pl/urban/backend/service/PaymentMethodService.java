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
        return paymentRepository.findByRestaurantsId(restaurantId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
