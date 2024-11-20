package pl.urban.backend.service;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.urban.backend.model.AddressSuggestions;
import pl.urban.backend.repository.AddressSuggestionsRepository;
import pl.urban.backend.request.GeoCodingResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
public class GeocodingService {
    private final RestTemplate restTemplate;
    private final AddressSuggestionsRepository addressSuggestionsRepository;

    public GeocodingService(RestTemplate restTemplate, AddressSuggestionsRepository addressSuggestionsRepository) {
        this.restTemplate = restTemplate;
        this.addressSuggestionsRepository = addressSuggestionsRepository;
    }

    public double[] getCoordinates(String address) {
        return addressSuggestionsRepository.findByName(address)
                .map(suggestion -> new double[]{suggestion.getLat(), suggestion.getLon()})
                .orElseGet(() -> fetchCoordinates(address));
    }
    public double[] fetchCoordinates(String address) {
        String formattedAddress = removeCommas(address);

        String url = String.format("https://nominatim.openstreetmap.org/search?format=json&q=%s",
                URLEncoder.encode(formattedAddress, StandardCharsets.UTF_8));
        try {
            ResponseEntity<GeoCodingResponse[]> response = restTemplate.getForEntity(url, GeoCodingResponse[].class);

            if (response.getBody() != null && response.getBody().length > 0) {
                GeoCodingResponse location = response.getBody()[0];
                double lat = Double.parseDouble(location.getLat());
                double lon = Double.parseDouble(location.getLon());

                AddressSuggestions newSuggest = new AddressSuggestions();
                newSuggest.setName(address);
                newSuggest.setLat(lat);
                newSuggest.setLon(lon);
                addressSuggestionsRepository.save(newSuggest);

                return new double[]{lat, lon};
            } else {
                throw new IllegalArgumentException("No results found for address: " + address);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find coordinates for address: " + address);
        }
    }

    private String removeCommas(String address) {
        String addressWithoutCommas = address.replace(",", "");
        return addressWithoutCommas
                .replace("ą", "a")
                .replace("ć", "c")
                .replace("ę", "e")
                .replace("ł", "l")
                .replace("ń", "n")
                .replace("ó", "o")
                .replace("ś", "s")
                .replace("ź", "z")
                .replace("ż", "z");

    }

}

