package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.dto.SuggestDTO;
import pl.urban.backend.model.AddressSuggestions;
import pl.urban.backend.repository.AddressSuggestionsRepository;

import java.util.List;

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


    public SuggestDTO convertToDTO(AddressSuggestions addressSuggestions) {
        SuggestDTO suggestDTO = new SuggestDTO();
        suggestDTO.setName(addressSuggestions.getName());
        return suggestDTO;
    }

}
