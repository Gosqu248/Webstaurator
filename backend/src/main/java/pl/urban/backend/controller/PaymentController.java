package pl.urban.backend.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.backend.model.Payment;
import pl.urban.backend.request.OrderRequest;
import pl.urban.backend.service.PayUService;
import pl.urban.backend.service.PaymentService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/getAll")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/getRestaurantPayments")
    public List<Payment> getAllPaymentForRestaurant(@RequestParam Long restaurantId) {
        return paymentService.getALlPaymentsByRestaurantId(restaurantId);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody OrderRequest orderRequest) {
        try {
            // Uzyskanie tokena OAuth
            String token = PayUService.getOAuthToken();
            if (token == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Nie udało się uzyskać tokena OAuth.");
            }

            // Przygotowanie danych zamówienia
            JSONObject orderData = new JSONObject();
            orderData.put("customerIp", orderRequest.getCustomerIp());
            orderData.put("merchantPosId", orderRequest.getMerchantPosId());
            orderData.put("description", orderRequest.getDescription());
            orderData.put("currencyCode", orderRequest.getCurrencyCode());
            orderData.put("totalAmount", orderRequest.getTotalAmount());

            // Przygotowanie listy produktów
            JSONArray productsArray = new JSONArray();
            for (OrderRequest.Product product : orderRequest.getProducts()) {
                JSONObject productData = new JSONObject();
                productData.put("name", product.getName());
                productData.put("unitPrice", product.getUnitPrice());
                productData.put("quantity", product.getQuantity());
                productsArray.put(productData);
            }
            orderData.put("products", productsArray);

            // Wywołanie metody do tworzenia zamówienia
            JSONObject response = PayUService.createOrder(token, orderData);

            // Sprawdzenie, czy redirectUri jest w odpowiedzi
            if (!response.has("redirectUri")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Odpowiedź nie zawiera redirectUri: " + response.toString());
            }

            // Pobranie redirectUri z odpowiedzi
            String redirectUri = response.getString("redirectUri");

            // Zwrócenie adresu URL do przekierowania
            return ResponseEntity.ok().body(redirectUri); // zwraca URL do przekierowania

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Błąd podczas tworzenia zamówienia: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Wystąpił nieoczekiwany błąd: " + e.getMessage());
        }
    }

}