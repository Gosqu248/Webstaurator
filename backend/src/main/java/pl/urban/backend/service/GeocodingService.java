package pl.urban.backend.service;


import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.urban.backend.request.GeoCodingResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {

    private static final String NOMINATIM_URL = "";
    private final RestTemplate restTemplate;


    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double[] getCoordinates(String address) throws JSONException {

        String url = String.format("https://nominatim.openstreetmap.org/search?q=%s&format=json&addressdetails=1",
                URLEncoder.encode(address, StandardCharsets.UTF_8));
        ResponseEntity<GeoCodingResponse[]> response = restTemplate.getForEntity(url, GeoCodingResponse[].class);

        if (response.getBody() != null && response.getBody().length > 0) {
            GeoCodingResponse location = response.getBody()[0];
            return new double[]{Double.parseDouble(location.getLat()), Double.parseDouble(location.getLon())};
        }
        throw new IllegalArgumentException("Could not find coordinates for address: " + address);
    }

}

