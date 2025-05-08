package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.urban.backend.model.Order;
import pl.urban.backend.model.OrderMenu;
import pl.urban.backend.dto.request.OrderPaymentRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayUService {

    @Value("https://secure.snd.payu.com/api/v2_1/orders")
    private String payuApiUrl;

    @Value("${payu.client.id}")
    private String clientId;

    @Value("${payu.client.secret}")
    private String clientSecret;


    private final RestTemplate restTemplate;
    private static final OkHttpClient client = new OkHttpClient();
    private static String accessToken = null;
    private static Instant tokenExpiration = null;

    public String getOAuthToken() {
        if (accessToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration)) {
            return accessToken;
        }

        RequestBody formBody = new okhttp3.FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        Request request = new Request.Builder()
                .url("https://secure.snd.payu.com/pl/standard/user/oauth/authorize")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();

            if (response.isSuccessful()) {
                JSONObject json = new JSONObject(responseBody);
                accessToken = json.getString("access_token");
                int expiresIn = json.getInt("expires_in");
                tokenExpiration = Instant.now().plusSeconds(expiresIn);
                return accessToken;
            } else {
                System.err.println("Błąd autoryzacji OAuth: " + response.message());
                return null;
            }
        } catch (JSONException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String,String> createOrder(Order order, String ip) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String token = getOAuthToken();
        headers.set("Authorization", "Bearer " + token);

        OrderPaymentRequest orderPaymentRequest = new OrderPaymentRequest();
        orderPaymentRequest.setCustomerIp(ip);
        orderPaymentRequest.setMerchantPosId(clientId);
        orderPaymentRequest.setDescription("Zamówienie z " + order.getRestaurant().getName());
        orderPaymentRequest.setCurrencyCode("PLN");
        orderPaymentRequest.setTotalAmount(String.valueOf((int) (order.getTotalPrice() * 100)));
        orderPaymentRequest.setContinueUrl("http://localhost:4200/payment-confirmation");

        List<OrderPaymentRequest.Product> products = getProducts(order);
        orderPaymentRequest.setProducts(products);


        HttpEntity<OrderPaymentRequest> entity = new HttpEntity<>(orderPaymentRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                payuApiUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (jsonResponse.has("redirectUri")) {
                String redirectUri = jsonResponse.getString("redirectUri");
                String orderId = jsonResponse.getString("orderId");

                if (orderId.isEmpty() || redirectUri.isEmpty()) {
                    throw new RuntimeException("Nie udało się utworzyć zamówienia");
                }

                return Map.of("redirectUri", redirectUri, "orderId", orderId);
            }
            throw new RuntimeException("Brak URL przekierowania w odpowiedzi PayU");
        } catch (JSONException e) {
            throw new RuntimeException("Błąd podczas przetwarzania odpowiedzi PayU", e);
        }
    }

    @NotNull
    private static List<OrderPaymentRequest.Product> getProducts(Order order) {
        List<OrderPaymentRequest.Product> products = new ArrayList<>();
        List<OrderMenu> orderMenus = order.getOrderMenus();
        for (OrderMenu orderMenu : orderMenus) {
            OrderPaymentRequest.Product product = new OrderPaymentRequest.Product();
            product.setName(orderMenu.getMenu().getName());
            product.setUnitPrice(String.valueOf((int) (orderMenu.getMenu().getPrice() * 100)));
            product.setQuantity(String.valueOf(orderMenu.getQuantity()));
            products.add(product);
        }
        return products;
    }

    public JSONObject getOrderById(String orderId) {
        String token = getOAuthToken();
        if (token == null) {
            throw new RuntimeException("Nie udało się uzyskać tokenu OAuth");
        }

        Request request = new Request.Builder()
                .url("https://secure.snd.payu.com/api/v2_1/orders/" + orderId)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Błąd podczas pobierania szczegółów zamówienia: " + response.message());
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            return new JSONObject(responseBody);
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas wykonywania żądania do API PayU", e);
        } catch (JSONException e) {
            throw new RuntimeException("Błąd podczas przetwarzania odpowiedzi JSON", e);
        }
    }

    public String getOrderStatus(String orderId) {
        String token = getOAuthToken();
        if (token == null) {
            throw new RuntimeException("Nie udało się uzyskać tokenu OAuth");
        }

        Request request = new Request.Builder()
                .url("https://secure.snd.payu.com/api/v2_1/orders/" + orderId)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Błąd podczas pobierania statusu zamówienia: " + response.message());
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray orders = jsonResponse.getJSONArray("orders");

            if (orders.length() > 0) {
                JSONObject order = orders.getJSONObject(0);
                return order.getString("status");
            } else {
                throw new RuntimeException("Brak zamówień w odpowiedzi");
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas wykonywania żądania do API PayU", e);
        } catch (JSONException e) {
            throw new RuntimeException("Błąd podczas przetwarzania odpowiedzi JSON", e);
        }
    }

}
