package com.gosqu.payment.payment;

import com.gosqu.payment.config.PayUProperties;
import com.gosqu.payment.exception.PayUException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayUClient {

    private final PayUProperties payUProperties;
    private final RestTemplate restTemplate;

    /**
     * Obtains a short-lived OAuth2 access token from PayU using client_credentials flow.
     */
    public String getPayUToken() {
        String tokenUrl = payUProperties.apiBaseUrl() + "/pl/standard/user/oauth/authorize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", payUProperties.clientId());
        body.add("client_secret", payUProperties.clientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(tokenUrl, request, Map.class);
            if (response == null || !response.containsKey("access_token")) {
                throw new PayUException("PayU token response missing access_token");
            }
            return (String) response.get("access_token");
        } catch (PayUException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PayUException("Failed to obtain PayU access token", ex);
        }
    }

    /**
     * Creates a PayU order and returns the redirect URL for the customer.
     *
     * @param orderId     internal order UUID
     * @param amount      total amount
     * @param customerEmail buyer email
     * @param notifyUrl   webhook URL for IPN callbacks
     * @return PayU redirect URL
     */
    public PayUOrderResult createOrder(UUID orderId, BigDecimal amount,
                                       String customerEmail, String notifyUrl) {
        String token = getPayUToken();
        String ordersUrl = payUProperties.apiBaseUrl() + "/api/v2_1/orders";

        // PayU expects amount in grosze (smallest currency unit)
        long amountInGrosze = amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        Map<String, Object> product = Map.of(
                "name", "Order #" + orderId,
                "unitPrice", String.valueOf(amountInGrosze),
                "quantity", "1");

        Map<String, Object> buyer = Map.of("email", customerEmail);

        Map<String, Object> orderRequest = Map.of(
                "notifyUrl", notifyUrl,
                "customerIp", "127.0.0.1",
                "merchantPosId", payUProperties.posId(),
                "description", "Order #" + orderId,
                "currencyCode", "PLN",
                "totalAmount", String.valueOf(amountInGrosze),
                "buyer", buyer,
                "products", List.of(product));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(ordersUrl, request, Map.class);
            if (response == null) {
                throw new PayUException("Empty response from PayU orders API");
            }
            String redirectUri = (String) response.get("redirectUri");
            String payuOrderId = (String) response.get("orderId");
            return new PayUOrderResult(payuOrderId, redirectUri);
        } catch (PayUException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PayUException("Failed to create PayU order for orderId=" + orderId, ex);
        }
    }

    public record PayUOrderResult(String payuOrderId, String redirectUri) {}
}
