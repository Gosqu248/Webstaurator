package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.response.PaymentResponse;
import pl.urban.backend.model.Payment;
import pl.urban.backend.repository.PaymentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {
    private final PaymentRepository paymentRepository;
    private final MapperService mapper;

    public List<PaymentResponse> getALlPaymentsByRestaurantId(Long restaurantId) {
        if (restaurantId <= 0) {
            throw new IllegalArgumentException("Invalid restaurant ID");
        }
        List<Payment> payments = paymentRepository.findByRestaurantsId(restaurantId);
        if (payments.isEmpty()) {
            throw new IllegalArgumentException("No payments found for the given restaurant ID");
        }
        return payments.stream()
                .map(mapper::fromPayment)
                .toList();
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(mapper::fromPayment)
                .toList();
    }
}
