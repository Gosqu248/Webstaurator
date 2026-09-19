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
import pl.urban.backend.dto.request.OrderMenuRequest;
import pl.urban.backend.dto.request.OrderRequest;
import pl.urban.backend.dto.request.OrderPaymentRequest;
import pl.urban.backend.model.Menu;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.repository.MenuRepository;
import pl.urban.backend.repository.RestaurantRepository;

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
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

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

    public Map<String,String> createOrder(OrderRequest order, String ip) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String token = getOAuthToken();
        headers.set("Authorization", "Bearer " + token);

        Restaurant restaurant = restaurantRepository.findById(order.restaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono restauracji o podanym ID"));

        OrderPaymentRequest orderPaymentRequest = new OrderPaymentRequest();
        orderPaymentRequest.setCustomerIp(ip);
        orderPaymentRequest.setMerchantPosId(clientId);
        orderPaymentRequest.setDescription("Zamówienie z " + restaurant.getName());
        orderPaymentRequest.setCurrencyCode("PLN");
        orderPaymentRequest.setTotalAmount(String.valueOf((int) (order.totalPrice() * 100)));
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
    private List<OrderPaymentRequest.Product> getProducts(OrderRequest orderRequest) {
        List<OrderPaymentRequest.Product> products = new ArrayList<>();
        List<OrderMenuRequest> orderMenuRequests = orderRequest.orderMenus();

        if (orderMenuRequests == null || orderMenuRequests.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one menu item");
        }

        for (OrderMenuRequest orderMenuRequest : orderMenuRequests) {
            if (orderMenuRequest == null || orderMenuRequest.menuId() == null) {
                throw new IllegalArgumentException("Menu ID is required for order items");
            }

            Menu menu = menuRepository.findById(orderMenuRequest.menuId())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono menu o podanym ID"));

            OrderPaymentRequest.Product product = new OrderPaymentRequest.Product();
            product.setName(menu.getName());
            product.setUnitPrice(String.valueOf((int) (menu.getPrice() * 100)));
            product.setQuantity(String.valueOf(orderMenuRequest.quantity()));
            products.add(product);
        }
        return products;
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
