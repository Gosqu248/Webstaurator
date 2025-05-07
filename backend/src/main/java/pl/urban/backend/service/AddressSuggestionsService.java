package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.dto.response.CoordinatesResponse;
import pl.urban.backend.dto.response.SuggestResponse;
import pl.urban.backend.model.AddressSuggestions;
import pl.urban.backend.repository.AddressSuggestionsRepository;

import java.util.List;

@Service
public class AddressSuggestionsService {

    private final AddressSuggestionsRepository addressSuggestionsRepository;
    private final GeocodingService geocodingService;

    public AddressSuggestionsService(AddressSuggestionsRepository addressSuggestionsRepository, GeocodingService geocodingService) {
        this.addressSuggestionsRepository = addressSuggestionsRepository;
        this.geocodingService = geocodingService;
    }

    public List<SuggestResponse> getSuggestions(String partialName) {
        List<AddressSuggestions> addressSuggestions = addressSuggestionsRepository.findTop5ByNameContainingIgnoreCase(partialName);
        return addressSuggestions.parallelStream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public CoordinatesResponse getCoordinates(String address) {
            double[] coords = geocodingService.getCoordinates(address);
            return new CoordinatesResponse(
                    coords[0],
                    coords[1]
            );
    }

    public SuggestResponse convertToDTO(AddressSuggestions addressSuggestions) {
        return new SuggestResponse(
                addressSuggestions.getName()
        );
    }



}
