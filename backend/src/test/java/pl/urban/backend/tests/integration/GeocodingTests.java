package pl.urban.backend.tests.integration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.urban.backend.service.GeocodingService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GeocodingTests {
    private static final Logger logger = LoggerFactory.getLogger(GeocodingTests.class);

    @Autowired
    private GeocodingService geocodingService;

    @Test
    public void testGetCoordinatesForParis() {
        String address = "Paris";
        double[] correctCoordinates = {48.8588897, 2.3200410217200766};

        double[] coordinates = geocodingService.getCoordinates(address);

        assertNotNull(coordinates);
        assertEquals(correctCoordinates[0], coordinates[0]);
        assertEquals(correctCoordinates[1], coordinates[1]);
        logger.info("Test dla adresu Paris zakończony sukcesem");

    }

    @Test
    public void testGetCoordinatesForKrakowska13Tarnow() {
        String address = "Krakowska 13, Tarnów";
        double[] correctCoordinates = {50.0111345, 20.982408484206346};

        double[] coordinates = geocodingService.getCoordinates(address);

        assertNotNull(coordinates);
        assertEquals(correctCoordinates[0], coordinates[0]);
        assertEquals(correctCoordinates[1], coordinates[1]);
        logger.info("Test dla adresu Krakowska 13, Tarnów zakończony sukcesem");
    }

    @Test
    public void shouldThrowExceptionForInvalidAddress() {
        String invalidAddress = "Ulica Nieistniejąca 999, Nigdzie";

        assertThatThrownBy(() -> geocodingService.getCoordinates(invalidAddress))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Could not find coordinates for address");
        logger.info("Test dla nieprawidłowego adresu zakończony sukcesem");

    }


}
