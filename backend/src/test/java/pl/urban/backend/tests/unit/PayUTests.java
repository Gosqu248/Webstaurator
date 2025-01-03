package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import pl.urban.backend.controller.PayUController;
import pl.urban.backend.model.Order;
import pl.urban.backend.service.PayUService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PayUTests {

    @Mock
    private PayUService payUService;

    @Mock
    private HttpServletRequest request;

    private PayUController payUController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        payUController = new PayUController(payUService);
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
        Order mockOrder = new Order();
        String mockIp = "127.0.0.1";
        Map<String, String> mockResponse = Map.of("redirectUri", "http://example.com", "orderId", "123");

        when(request.getRemoteAddr()).thenReturn(mockIp);
        when(payUService.createOrder(mockOrder, mockIp)).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = payUController.createPayment(mockOrder, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(payUService, times(1)).createOrder(mockOrder, mockIp);
    }

    @Test
    void shouldHandleCreatePaymentException() {
        Order mockOrder = new Order();
        String mockIp = "127.0.0.1";

        when(request.getRemoteAddr()).thenReturn(mockIp);
        when(payUService.createOrder(mockOrder, mockIp)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Map<String, String>> response = payUController.createPayment(mockOrder, request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error", response.getBody().get("error"));
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
