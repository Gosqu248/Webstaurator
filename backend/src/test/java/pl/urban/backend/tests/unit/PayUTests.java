package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import pl.urban.backend.controller.PayUController;
import pl.urban.backend.dto.request.OrderMenuRequest;
import pl.urban.backend.dto.request.OrderRequest;
import pl.urban.backend.enums.OrderStatus;
import pl.urban.backend.service.PayUService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PayUTests {

    @Mock
    private PayUService payUService;

    @Mock
    private HttpServletRequest request;

    private PayUController payUController;
    private OrderRequest sampleOrderRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        payUController = new PayUController(payUService);

        // Initialize sample OrderRequest for tests
        sampleOrderRequest = createSampleOrderRequest();
    }

    private OrderRequest createSampleOrderRequest() {
        OrderMenuRequest menuItem1 = new OrderMenuRequest(
                2,
                1L,
                List.of(1L, 2L)
        );

        OrderMenuRequest menuItem2 = new OrderMenuRequest(
                1,
                3L,
                List.of(4L)
        );

        return new OrderRequest(
                1L,
                2L,
                3L,
                List.of(menuItem1, menuItem2),
                "Delivery",
                "12:00-13:00",
                null,
                "Card",
                OrderStatus.niezapłacone,
                55.90,
                "Please deliver to the side entrance"
        );
    }

    @Test
    void shouldReturnAuthToken() {
        String mockToken = "mockToken";
        when(payUService.getOAuthToken()).thenReturn(mockToken);

        ResponseEntity<String> response = payUController.getTokens();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockToken, response.getBody());
        verify(payUService, times(1)).getOAuthToken();
    }

    @Test
    void shouldCreatePaymentSuccessfully() {
        String mockIp = "127.0.0.1";
        Map<String, String> mockResponse = Map.of("redirectUri", "http://example.com", "orderId", "123");

        when(request.getRemoteAddr()).thenReturn(mockIp);
        when(payUService.createOrder(sampleOrderRequest, mockIp)).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = payUController.createPayment(sampleOrderRequest, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(payUService, times(1)).createOrder(sampleOrderRequest, mockIp);
    }

    @Test
    void shouldHandleCreatePaymentException() {
        String mockIp = "127.0.0.1";

        when(request.getRemoteAddr()).thenReturn(mockIp);
        when(payUService.createOrder(sampleOrderRequest, mockIp)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Map<String, String>> response = payUController.createPayment(sampleOrderRequest, request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error", Objects.requireNonNull(response.getBody()).get("error"));
    }

    @Test
    void shouldGetPaymentStatusSuccessfully() {
        String mockOrderId = "123";
        String mockStatus = "COMPLETED";

        when(payUService.getOrderStatus(mockOrderId)).thenReturn(mockStatus);

        ResponseEntity<String> response = payUController.getPayUOrderStatus(mockOrderId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockStatus, response.getBody());
    }

    @Test
    void shouldHandleGetPaymentStatusException() {
        String mockOrderId = "123";

        when(payUService.getOrderStatus(mockOrderId)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = payUController.getPayUOrderStatus(mockOrderId);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error", response.getBody());
    }

    @Test
    void shouldReturnValidAuthToken() {
        String validToken = "validToken";
        when(payUService.getOAuthToken()).thenReturn(validToken);

        ResponseEntity<String> response = payUController.getTokens();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(validToken, response.getBody());
        verify(payUService, times(1)).getOAuthToken();
    }
}
