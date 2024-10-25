package pl.urban.backend.service;

import okhttp3.*;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import org.json.JSONObject;

@Service
public class PayUService {

    private static OkHttpClient client = new OkHttpClient();
    private static String accessToken = null;
    private  static Instant tokenExpiration = null;

    public static String getOAuthToken() throws IOException {

        if (accessToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration)) {
            return accessToken;
        }

        String clientId = "485725";
        String clientSecret = "0d35725950bdfbb77ee5d67f6779a8f9";

        RequestBody formBody = new okhttp3.FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        Request request = new Request.Builder()
                .url("https://secure.snd.payu.com/pl/standard/user/oauth/authorize")
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseBody = response.body().string();
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
            throw new RuntimeException(e);
        }
    }



    public static JSONObject createOrder(String token, JSONObject orderData) throws IOException, JSONException {
        RequestBody requestBody = RequestBody.create(orderData.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://secure.snd.payu.com/api/v2_1/orders")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseBody = response.body().string();
                System.out.println("Odpowiedź PayU: " + responseBody); // Dodano logowanie odpowiedzi

                return new JSONObject(responseBody);
            } else {
                System.err.println("Błąd tworzenia zamówienia: " + response.message());
                System.err.println("Treść odpowiedzi: " + response.body().string()); // Dodano logowanie błędu
                throw new IOException("Błąd tworzenia zamówienia: " + response.message());
            }
        }
    }


}
