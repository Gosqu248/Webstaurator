package pl.urban.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.urban.backend.dto.SuggestDTO;

import pl.urban.backend.service.AddressSuggestionsService;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
public class AddressSuggestionsController {

    private final AddressSuggestionsService addressSuggestionsService;

    public AddressSuggestionsController(AddressSuggestionsService addressSuggestionsService) {
        this.addressSuggestionsService = addressSuggestionsService;
    }

    @GetMapping("/get")
    public List<SuggestDTO> getSuggestions(@RequestParam String partialName) {
        return addressSuggestionsService.getSuggestions(partialName);
    }
}
