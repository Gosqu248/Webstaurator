package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.dto.CoordinatesDTO;
import pl.urban.backend.dto.SuggestDTO;
import pl.urban.backend.model.AddressSuggestions;
import pl.urban.backend.repository.AddressSuggestionsRepository;
import pl.urban.backend.request.GeoCodingResponse;

import java.util.List;
import java.util.Optional;

@Service
public class AddressSuggestionsService {

    private final AddressSuggestionsRepository addressSuggestionsRepository;

    public AddressSuggestionsService(AddressSuggestionsRepository addressSuggestionsRepository) {
        this.addressSuggestionsRepository = addressSuggestionsRepository;
    }

    public List<SuggestDTO> getSuggestions(String partialName) {
        List<AddressSuggestions> addressSuggestions = addressSuggestionsRepository.findTop5ByNameContainingIgnoreCase(partialName);
        return addressSuggestions.parallelStream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public CoordinatesDTO getCoordinates(String address) {
        Optional<AddressSuggestions> addressSuggestions = addressSuggestionsRepository.findByName(address);
        return addressSuggestions.map(this::convertToCoordinatesDTO).orElseThrow(() -> new IllegalArgumentException("Could not find coordinates for address: " + address));
    }

    public SuggestDTO convertToDTO(AddressSuggestions addressSuggestions) {
        SuggestDTO suggestDTO = new SuggestDTO();
        suggestDTO.setName(addressSuggestions.getName());
        return suggestDTO;
    }

    public CoordinatesDTO convertToCoordinatesDTO(AddressSuggestions addressSuggestions) {
        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();

        coordinatesDTO.setLat(addressSuggestions.getLat());
        coordinatesDTO.setLon(addressSuggestions.getLon());
        return coordinatesDTO;
    }

}
