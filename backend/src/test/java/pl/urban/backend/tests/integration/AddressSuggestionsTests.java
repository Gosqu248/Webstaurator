package pl.urban.backend.tests.integration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.urban.backend.dto.CoordinatesDTO;
import pl.urban.backend.dto.SuggestDTO;
import pl.urban.backend.service.AddressSuggestionsService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AddressSuggestionsTests {

    private static final Logger logger = LoggerFactory.getLogger(AddressSuggestionsTests.class);

    @Autowired
    private AddressSuggestionsService addressSuggestionsService;

    @Test
    public void testGetSuggestions() {
        String partialName = "Krakowska";

        SuggestDTO suggest1 = new SuggestDTO();
        suggest1.setName("Krakowska 13, Tarnów");


        List<SuggestDTO> expectedSuggestions = List.of(suggest1);

        List<SuggestDTO> actualSuggestions = addressSuggestionsService.getSuggestions(partialName);

        assertNotNull(actualSuggestions);
        assertEquals(expectedSuggestions.size(), actualSuggestions.size());
        assertEquals(expectedSuggestions.getFirst().getName(), actualSuggestions.getFirst().getName());
        logger.info("Test dla podpowiedzi dla '{}'' zakończony sukcesem", partialName);
    }

    @Test
    public void testGetCoordinatesForAddressSpokojna10Zakliczyn() {
        String address = "Spokojna 10, Zakliczyn";
        CoordinatesDTO expectedCoordinates = new CoordinatesDTO();
        expectedCoordinates.setLat(49.85496906411742);
        expectedCoordinates.setLon(20.804921425415863);

        CoordinatesDTO actualCoordinates = addressSuggestionsService.getCoordinates(address);

        assertNotNull(actualCoordinates);
        assertEquals(expectedCoordinates.getLat(), actualCoordinates.getLat());
        assertEquals(expectedCoordinates.getLon(), actualCoordinates.getLon());
        logger.info("Test dla współrzędnych dla '{}'' zakończony sukcesem", address);
    }

    @Test
    public void testGetCoordinatesForKrakowska13Tarnow() {
        String address = "Krakowska 13, Tarnów";
        CoordinatesDTO expectedCoordinates = new CoordinatesDTO();
        expectedCoordinates.setLat(50.0111345);
        expectedCoordinates.setLon(20.982408484206346);

        CoordinatesDTO actualCoordinates = addressSuggestionsService.getCoordinates(address);

        assertNotNull(actualCoordinates);
        assertEquals(expectedCoordinates.getLat(), actualCoordinates.getLat());
        assertEquals(expectedCoordinates.getLon(), actualCoordinates.getLon());
        logger.info("Test dla współrzędnych dla '{}'' zakończony sukcesem", address);
    }

    @Test
    public void shouldThrowExceptionForInvalidAddress() {
        String invalidAddress = "Nieistniejący adres";

        assertThatThrownBy(() -> addressSuggestionsService.getCoordinates(invalidAddress))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Could not find coordinates for address");
        logger.info("Test dla nieprawidłowego adresu '{}' zakończony sukcesem", invalidAddress);
    }
}
