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
    private final GeocodingService geocodingService;

    public AddressSuggestionsService(AddressSuggestionsRepository addressSuggestionsRepository, GeocodingService geocodingService) {
        this.addressSuggestionsRepository = addressSuggestionsRepository;
        this.geocodingService = geocodingService;
    }

    public List<SuggestDTO> getSuggestions(String partialName) {
        List<AddressSuggestions> addressSuggestions = addressSuggestionsRepository.findTop5ByNameContainingIgnoreCase(partialName);
        return addressSuggestions.parallelStream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public CoordinatesDTO getCoordinates(String address) {
            double[] coords = geocodingService.getCoordinates(address);
            CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
            coordinatesDTO.setLat(coords[0]);
            coordinatesDTO.setLon(coords[1]);
            return coordinatesDTO;
    }

    public SuggestDTO convertToDTO(AddressSuggestions addressSuggestions) {
        SuggestDTO suggestDTO = new SuggestDTO();
        suggestDTO.setName(addressSuggestions.getName());
        return suggestDTO;
    }



}
