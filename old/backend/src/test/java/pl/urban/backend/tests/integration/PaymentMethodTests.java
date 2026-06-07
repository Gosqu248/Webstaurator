package pl.urban.backend.tests.integration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.urban.backend.dto.response.PaymentResponse;
import pl.urban.backend.model.Payment;
import pl.urban.backend.service.PaymentMethodService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PaymentMethodTests {

    private static final Logger logger = LoggerFactory.getLogger(PaymentMethodTests.class);

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Test
    public void testGetAllPaymentsForRestaurant() {
        // Given
        Long restaurantId = 1L;

        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setMethod("Gotówka");

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setMethod("PayU");

        List<Payment> expectedPayments = List.of(payment1, payment2);

        // When
        List<PaymentResponse> actualPayments = paymentMethodService.getALlPaymentsByRestaurantId(restaurantId);

        // Then
        assertNotNull(actualPayments);
        assertEquals(expectedPayments.size(), actualPayments.size());
        assertEquals(expectedPayments.get(0).getMethod(), actualPayments.get(0).method());
        assertEquals(expectedPayments.get(1).getMethod(), actualPayments.get(1).method());
        logger.info("Test dla płatności dla restauracji z ID '{}' zakończony sukcesem", restaurantId);
    }

    @Test
    public void testGetAllPayments() {
        // Given
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setMethod("Gotówka");

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setMethod("PayU");

        List<Payment> expectedPayments = List.of(payment1, payment2);

        // When
        List<PaymentResponse> actualPayments = paymentMethodService.getAllPayments();

        // Then
        assertNotNull(actualPayments);
        assertEquals(expectedPayments.size(), actualPayments.size());
        assertEquals(expectedPayments.get(0).getMethod(), actualPayments.get(0).method());
        assertEquals(expectedPayments.get(1).getMethod(), actualPayments.get(1).method());
        logger.info("Test dla wszystkich płatności zakończony sukcesem");
    }

    @Test
    public void shouldThrowExceptionForInvalidRestaurantId() {
        // Given
        Long invalidRestaurantId = -1L;

        // Then
        assertThatThrownBy(() -> paymentMethodService.getALlPaymentsByRestaurantId(invalidRestaurantId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid restaurant ID");
        logger.info("Test dla nieprawidłowego ID restauracji '{}' zakończony sukcesem", invalidRestaurantId);
    }
}
