package pl.urban.backend.tests.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.urban.backend.service.GeocodingService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GeocodingTests {

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
    }

    @Test
    public void testGetCoordinatesForKrakowska13Tarnow() {
        String address = "Krakowska 13, Tarnów";
        double[] correctCoordinates = {50.0111345, 20.982408484206346};

        double[] coordinates = geocodingService.getCoordinates(address);

        assertNotNull(coordinates);
        assertEquals(correctCoordinates[0], coordinates[0]);
        assertEquals(correctCoordinates[1], coordinates[1]);
    }

    @Test
    public void shouldThrowExceptionForInvalidAddress() {
        String invalidAddress = "Ulica Nieistniejąca 999, Nigdzie";

        assertThatThrownBy(() -> geocodingService.getCoordinates(invalidAddress))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Could not find coordinates for address");

    }


}
