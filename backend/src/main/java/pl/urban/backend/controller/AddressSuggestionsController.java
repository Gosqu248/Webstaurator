package pl.urban.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.urban.backend.dto.response.CoordinatesResponse;

import pl.urban.backend.dto.response.SuggestResponse;
import pl.urban.backend.service.AddressSuggestionsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suggestions")
public class AddressSuggestionsController {
    private final AddressSuggestionsService addressSuggestionsService;

    @GetMapping
    public List<SuggestResponse> getSuggestions(@RequestParam String partialName) {
        return addressSuggestionsService.getSuggestions(partialName);
    }

    @GetMapping("/coordinates")
    public CoordinatesResponse getCoordinates(@RequestParam String address) {
        return addressSuggestionsService.getCoordinates(address);
    }
}
