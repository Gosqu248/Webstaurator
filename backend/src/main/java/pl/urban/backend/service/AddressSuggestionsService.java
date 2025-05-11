package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.response.CoordinatesResponse;
import pl.urban.backend.dto.response.SuggestResponse;
import pl.urban.backend.model.AddressSuggestions;
import pl.urban.backend.repository.AddressSuggestionsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressSuggestionsService {
    private final AddressSuggestionsRepository addressSuggestionsRepository;
    private final GeocodingService geocodingService;

    public List<SuggestResponse> getSuggestions(String partialName) {
        List<AddressSuggestions> addressSuggestions = addressSuggestionsRepository.findTop5ByNameContainingIgnoreCase(partialName);
        return addressSuggestions.parallelStream()
                .map(this::toAddressSuggestions)
                .collect(java.util.stream.Collectors.toList());
    }

    public CoordinatesResponse getCoordinates(String address) {
            double[] coords = geocodingService.getCoordinates(address);
            return new CoordinatesResponse(
                    coords[0],
                    coords[1]
            );
    }

    public SuggestResponse toAddressSuggestions(AddressSuggestions addressSuggestions) {
        return new SuggestResponse(
                addressSuggestions.getName()
        );
    }



}
