package pl.urban.backend.service;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.urban.backend.request.GeoCodingResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeocodingService {
    private final RestTemplate restTemplate;
    private final Map<String, double[]> geocodeCache = new ConcurrentHashMap<>();

    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double[] getCoordinates(String address) {
        return geocodeCache.computeIfAbsent(address, this::fetchCoordinates);
    }
    public double[] fetchCoordinates(String address) {
        String url = String.format("https://nominatim.openstreetmap.org/search?format=json&q=%s",
                URLEncoder.encode(address, StandardCharsets.UTF_8));
        try {
            ResponseEntity<GeoCodingResponse[]> response = restTemplate.getForEntity(url, GeoCodingResponse[].class);

            if (response.getBody() != null && response.getBody().length > 0) {
                GeoCodingResponse location = response.getBody()[0];
                return new double[]{Double.parseDouble(location.getLat()), Double.parseDouble(location.getLon())};
            } else {
                throw new IllegalArgumentException("No results found for address: " + address);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find coordinates for address: " + address);
        }

    }

}

