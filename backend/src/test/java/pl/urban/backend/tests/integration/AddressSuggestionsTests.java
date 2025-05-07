package pl.urban.backend.tests.integration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.urban.backend.dto.response.CoordinatesResponse;
import pl.urban.backend.dto.response.SuggestResponse;
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

        SuggestResponse suggest1 = new SuggestResponse(
                "Krakowska 13, Tarnów"
        );


        List<SuggestResponse> expectedSuggestions = List.of(suggest1);

        List<SuggestResponse> actualSuggestions = addressSuggestionsService.getSuggestions(partialName);

        assertNotNull(actualSuggestions);
        assertEquals(expectedSuggestions.size(), actualSuggestions.size());
        assertEquals(expectedSuggestions.getFirst().name(), actualSuggestions.getFirst().name());
        logger.info("Test dla podpowiedzi dla '{}'' zakończony sukcesem", partialName);
    }

    @Test
    public void testGetCoordinatesForAddressSpokojna10Zakliczyn() {
        String address = "Spokojna 10, Zakliczyn";
        CoordinatesResponse expectedCoordinates = new CoordinatesResponse(
                49.85496906411742,
                20.804921425415863
        );

        CoordinatesResponse actualCoordinates = addressSuggestionsService.getCoordinates(address);

        assertNotNull(actualCoordinates);
        assertEquals(expectedCoordinates.lat(), actualCoordinates.lat());
        assertEquals(expectedCoordinates.lon(), actualCoordinates.lon());
        logger.info("Test dla współrzędnych dla '{}'' zakończony sukcesem", address);
    }

    @Test
    public void testGetCoordinatesForKrakowska13Tarnow() {
        String address = "Krakowska 13, Tarnów";
        CoordinatesResponse expectedCoordinates = new CoordinatesResponse(
                50.0111345,
                20.982408484206346
        );

        CoordinatesResponse actualCoordinates = addressSuggestionsService.getCoordinates(address);

        assertNotNull(actualCoordinates);
        assertEquals(expectedCoordinates.lat(), actualCoordinates.lat());
        assertEquals(expectedCoordinates.lon(), actualCoordinates.lon());
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
