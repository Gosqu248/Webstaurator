package com.gosqu.restaurant.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gosqu.restaurant.common.exception.ForbiddenException;
import com.gosqu.restaurant.common.exception.GlobalExceptionHandler;
import com.gosqu.restaurant.restaurant.dto.request.RestaurantRequest;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import com.gosqu.restaurant.restaurant.exception.RestaurantNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    static final UUID RESTAURANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    static final UUID OWNER_ID      = UUID.fromString("00000000-0000-0000-0000-000000000010");
    static final UUID OTHER_ID      = UUID.fromString("00000000-0000-0000-0000-000000000099");

    @Mock
    RestaurantService restaurantService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new RestaurantController(restaurantService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private RestaurantResponse sampleResponse() {
        return new RestaurantResponse(RESTAURANT_ID, "Testowa", null, "ITALIAN", "ul. Testowa 1",
                "Kraków", null, null, null, null, null, true, 0.0, 30,
                new BigDecimal("5.00"), new BigDecimal("20.00"));
    }

    private RestaurantRequest validRequest() {
        return new RestaurantRequest("Testowa", null, CuisineType.PIZZA,
                "ul. Testowa 1", "Kraków", null, null, null, 30,
                new BigDecimal("5.00"), new BigDecimal("20.00"));
    }

    @Nested
    @DisplayName("GET /restaurants")
    class SearchRestaurants {

        @Test
        @DisplayName("returns 200 without query parameters")
        void search_noParams_returns200() throws Exception {
            when(restaurantService.search(any(), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

            mockMvc.perform(get("/restaurants"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 400 when service throws IllegalArgumentException for invalid enum")
        void search_invalidCuisineType_returns400() throws Exception {
            when(restaurantService.search(any(), eq("INVALID"), any(), any()))
                    .thenThrow(new IllegalArgumentException("No enum constant: INVALID"));

            mockMvc.perform(get("/restaurants").param("cuisineType", "INVALID"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    @DisplayName("GET /restaurants/{id}")
    class GetById {

        @Test
        @DisplayName("returns 200 with response body when restaurant exists")
        void getById_exists_returns200() throws Exception {
            when(restaurantService.getById(RESTAURANT_ID)).thenReturn(sampleResponse());

            mockMvc.perform(get("/restaurants/" + RESTAURANT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(RESTAURANT_ID.toString()))
                    .andExpect(jsonPath("$.name").value("Testowa"));
        }

        @Test
        @DisplayName("returns 404 when restaurant not found")
        void getById_missing_returns404() throws Exception {
            when(restaurantService.getById(OTHER_ID))
                    .thenThrow(new RestaurantNotFoundException(OTHER_ID));

            mockMvc.perform(get("/restaurants/" + OTHER_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    @DisplayName("POST /restaurants")
    class CreateRestaurant {

        @Test
        @DisplayName("returns 201 with valid payload")
        void create_valid_returns201() throws Exception {
            when(restaurantService.create(any(UUID.class), any())).thenReturn(sampleResponse());

            mockMvc.perform(post("/restaurants")
                            .header("X-User-Id", OWNER_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Testowa"));
        }

        @Test
        @DisplayName("returns 400 when name is blank (Bean Validation)")
        void create_missingName_returns400() throws Exception {
            var invalid = new RestaurantRequest("", null, CuisineType.PIZZA,
                    "ul. Testowa 1", "Kraków", null, null, null, null, null, null);

            mockMvc.perform(post("/restaurants")
                            .header("X-User-Id", OWNER_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    @DisplayName("PUT /restaurants/{id}")
    class UpdateRestaurant {

        @Test
        @DisplayName("returns 403 when different owner triggers ForbiddenException")
        void update_differentOwner_returns403() throws Exception {
            when(restaurantService.update(any(UUID.class), any(UUID.class), any()))
                    .thenThrow(new ForbiddenException());

            mockMvc.perform(put("/restaurants/" + RESTAURANT_ID)
                            .header("X-User-Id", OTHER_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /restaurants/{id}/rating")
    class UpdateRating {

        @Test
        @DisplayName("returns 204 with valid internal service header")
        void updateRating_validHeader_returns204() throws Exception {
            mockMvc.perform(put("/restaurants/" + RESTAURANT_ID + "/rating")
                            .header("X-Internal-Service", "review-service")
                            .param("rating", "4.5"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 403 with invalid internal service header")
        void updateRating_invalidHeader_returns403() throws Exception {
            mockMvc.perform(put("/restaurants/" + RESTAURANT_ID + "/rating")
                            .header("X-Internal-Service", "unknown-service")
                            .param("rating", "4.5"))
                    .andExpect(status().isForbidden());
        }
    }
}
