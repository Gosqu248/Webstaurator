package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.urban.backend.dto.CoordinatesDTO;
import pl.urban.backend.dto.SuggestDTO;
import pl.urban.backend.model.AddressSuggestions;
import pl.urban.backend.repository.AddressSuggestionsRepository;
import pl.urban.backend.service.AddressSuggestionsService;
import pl.urban.backend.service.GeocodingService;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AddressSuggestionsTest {

    private static final Logger logger = LoggerFactory.getLogger(AddressSuggestionsTest.class);

    @InjectMocks
    private AddressSuggestionsService addressSuggestionsService;

    @Mock
    private AddressSuggestionsRepository addressSuggestionsRepository;

    @Mock
    private GeocodingService geocodingService;

    private AddressSuggestions addressSuggestions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        addressSuggestions = new AddressSuggestions();
        addressSuggestions.setName("Test Address");

        SuggestDTO suggestDTO = new SuggestDTO();
        suggestDTO.setName("Test Address");
    }

    @Test
    void testGetSuggestions() {
        logger.info("Running testGetSuggestions");

        when(addressSuggestionsRepository.findTop5ByNameContainingIgnoreCase(eq("Test"))).thenReturn(List.of(addressSuggestions));

        List<SuggestDTO> result = addressSuggestionsService.getSuggestions("Test");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Test Address", result.getFirst().getName());
        verify(addressSuggestionsRepository, times(1)).findTop5ByNameContainingIgnoreCase(eq("Test"));
        logger.info("Completed testGetSuggestions");
    }

    @Test
    void testGetCoordinates() {
        logger.info("Running testGetCoordinates");

        double[] mockCoordinates = { 51.5074, 0.1278 };
        when(geocodingService.getCoordinates(eq("Test Address"))).thenReturn(mockCoordinates);

        CoordinatesDTO result = addressSuggestionsService.getCoordinates("Test Address");

        assertNotNull(result);
        assertEquals(51.5074, result.getLat());
        assertEquals(0.1278, result.getLon());
        verify(geocodingService, times(1)).getCoordinates(eq("Test Address"));
        logger.info("Completed testGetCoordinates");
    }

    @Test
    void testConvertToDTO() {
        logger.info("Running testConvertToDTO");

        SuggestDTO result = addressSuggestionsService.convertToDTO(addressSuggestions);

        assertNotNull(result);
        assertEquals("Test Address", result.getName());
        logger.info("Completed testConvertToDTO");
    }
}
