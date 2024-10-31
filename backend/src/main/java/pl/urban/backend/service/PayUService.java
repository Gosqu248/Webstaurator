package pl.urban.backend.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
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
import pl.urban.backend.request.OrderRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayUService {

    @Value("${payu.api.url}")
    private String payuApiUrl;

    @Value("${payu.client.id}")
    private String clientId;

    @Value("${payu.client.secret}")
    private String clientSecret;

    @Value("${payu.api.token}")
    private String payuApiToken;

    private final RestTemplate restTemplate;
    private static OkHttpClient client = new OkHttpClient();
    private static String accessToken = null;
    private static Instant tokenExpiration = null;

    public PayUService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
            String responseBody = response.body().string();
            System.out.println("OAuth Token Response: " + responseBody);

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

    public String createOrder(Order order, String ip) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + payuApiToken);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerIp(ip);
        orderRequest.setMerchantPosId("145227");
        orderRequest.setDescription("Zamówienie z " + order.getRestaurant().getName());
        orderRequest.setCurrencyCode("PLN");
        orderRequest.setTotalAmount(String.valueOf((int) (order.getTotalPrice() * 100))); // Convert to smallest currency unit

        List<OrderRequest.Product> products = getProducts(order);
        orderRequest.setProducts(products);


        HttpEntity<OrderRequest> entity = new HttpEntity<>(orderRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                payuApiUrl + "/orders",
                HttpMethod.POST,
                entity,
                String.class
        );

        // Parsuj odpowiedź JSON i wyciągnij URL przekierowania
        try {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (jsonResponse.has("redirectUri")) {
                return jsonResponse.getString("redirectUri");
            }
            throw new RuntimeException("Brak URL przekierowania w odpowiedzi PayU");
        } catch (JSONException e) {
            throw new RuntimeException("Błąd podczas przetwarzania odpowiedzi PayU", e);
        }
    }

    @NotNull
    private static List<OrderRequest.Product> getProducts(Order order) {
        List<OrderRequest.Product> products = new ArrayList<>();
        List<OrderMenu> orderMenus = order.getOrderMenus();
        for (OrderMenu orderMenu : orderMenus) {
            OrderRequest.Product product = new OrderRequest.Product();
            product.setName(orderMenu.getMenu().getName());
            product.setUnitPrice(String.valueOf((int) (orderMenu.getMenu().getPrice() * 100))); // Convert to smallest currency unit
            product.setQuantity(String.valueOf(orderMenu.getQuantity()));
            products.add(product);
        }
        return products;
    }
}