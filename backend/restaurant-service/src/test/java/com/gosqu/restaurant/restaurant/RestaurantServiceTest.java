package com.gosqu.restaurant.restaurant;

import com.gosqu.restaurant.common.exception.ForbiddenException;
import com.gosqu.restaurant.restaurant.dto.request.RestaurantRequest;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import com.gosqu.restaurant.restaurant.exception.RestaurantNotFoundException;
import com.gosqu.restaurant.restaurant.mapper.RestaurantMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    static final UUID ID_1     = UUID.fromString("00000000-0000-0000-0000-000000000001");
    static final UUID OWNER_10 = UUID.fromString("00000000-0000-0000-0000-000000000010");
    static final UUID OWNER_99 = UUID.fromString("00000000-0000-0000-0000-000000000099");

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @Mock
    private RestaurantEventPublisher eventPublisher;

    @InjectMocks
    private RestaurantService service;

    private Restaurant restaurant(UUID id, UUID ownerId) {
        return Restaurant.builder()
                .id(id).ownerId(ownerId).name("Testowa Restauracja")
                .cuisineType(CuisineType.PIZZA).address("ul. Testowa 1").city("Kraków")
                .isActive(true).avgRating(0.0).build();
    }

    private RestaurantRequest restaurantRequest() {
        return new RestaurantRequest("Testowa", null, CuisineType.PIZZA,
                "ul. Testowa 1", "Kraków", null, null, null, 30,
                new BigDecimal("5.00"), new BigDecimal("20.00"));
    }

    private RestaurantResponse responseFor(UUID id, String name) {
        return new RestaurantResponse(id, name, null, "PIZZA", "ul. Testowa 1", "Kraków",
                null, null, null, null, null, true, 0.0, 30,
                new BigDecimal("5.00"), new BigDecimal("20.00"));
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("returns response when restaurant exists")
        void getById_exists_returnsResponse() {
            var r = restaurant(ID_1, OWNER_10);
            var expected = responseFor(ID_1, "Testowa Restauracja");
            when(restaurantRepository.findById(ID_1)).thenReturn(Optional.of(r));
            when(restaurantMapper.toResponse(r)).thenReturn(expected);

            RestaurantResponse result = service.getById(ID_1);

            assertThat(result.id()).isEqualTo(ID_1);
            assertThat(result.name()).isEqualTo("Testowa Restauracja");
        }

        @Test
        @DisplayName("throws RestaurantNotFoundException when not found")
        void getById_missing_throws() {
            when(restaurantRepository.findById(OWNER_99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(OWNER_99))
                    .isInstanceOf(RestaurantNotFoundException.class)
                    .hasMessageContaining(OWNER_99.toString());
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves and returns response")
        void create_validRequest_savesAndReturns() {
            var r = restaurant(ID_1, OWNER_10);
            var expected = responseFor(ID_1, "Testowa Restauracja");
            when(restaurantMapper.toEntity(any())).thenReturn(r);
            when(restaurantRepository.save(r)).thenReturn(r);
            when(restaurantMapper.toResponse(r)).thenReturn(expected);

            RestaurantResponse result = service.create(OWNER_10, restaurantRequest());

            assertThat(result.name()).isEqualTo("Testowa Restauracja");
            verify(restaurantRepository).save(r);
            verify(eventPublisher).publishCreated(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates when owner matches")
        void update_ownerMatches_updates() {
            var r = restaurant(ID_1, OWNER_10);
            var expected = responseFor(ID_1, "Testowa Restauracja");
            when(restaurantRepository.findById(ID_1)).thenReturn(Optional.of(r));
            when(restaurantRepository.save(r)).thenReturn(r);
            when(restaurantMapper.toResponse(r)).thenReturn(expected);

            service.update(OWNER_10, ID_1, restaurantRequest());

            verify(restaurantRepository).save(r);
            verify(eventPublisher).publishUpdated(any());
        }

        @Test
        @DisplayName("throws ForbiddenException when different owner")
        void update_differentOwner_throwsForbidden() {
            var r = restaurant(ID_1, OWNER_10);
            when(restaurantRepository.findById(ID_1)).thenReturn(Optional.of(r));

            assertThatThrownBy(() -> service.update(OWNER_99, ID_1, restaurantRequest()))
                    .isInstanceOf(ForbiddenException.class);

            verify(restaurantRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("search")
    class Search {

        @Test
        @DisplayName("delegates to repository with nulls when no filters given")
        void search_noFilters_delegatesWithNulls() {
            var pageable = PageRequest.of(0, 20);
            when(restaurantRepository.search(null, null, null, pageable))
                    .thenReturn(new PageImpl<>(List.of()));

            Page<RestaurantResponse> result = service.search(null, null, null, pageable);

            assertThat(result).isEmpty();
            verify(restaurantRepository).search(null, null, null, pageable);
        }

        @Test
        @DisplayName("converts cuisineType string to enum")
        void search_withCuisineType_convertsEnum() {
            var pageable = PageRequest.of(0, 20);
            when(restaurantRepository.search(null, CuisineType.PIZZA, null, pageable))
                    .thenReturn(new PageImpl<>(List.of()));

            service.search(null, "PIZZA", null, pageable);

            verify(restaurantRepository).search(null, CuisineType.PIZZA, null, pageable);
        }

        @Test
        @DisplayName("throws IllegalArgumentException for invalid cuisineType")
        void search_invalidCuisineType_throwsIllegalArgument() {
            assertThatThrownBy(() -> service.search(null, "INVALID_TYPE", null, PageRequest.of(0, 20)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("updateAvgRating")
    class UpdateAvgRating {

        @Test
        @DisplayName("updates rating atomically when restaurant exists")
        void updateAvgRating_exists_updatesRating() {
            when(restaurantRepository.existsById(ID_1)).thenReturn(true);

            service.updateAvgRating(ID_1, 4.5);

            verify(restaurantRepository).updateAvgRating(ID_1, 4.5);
        }

        @Test
        @DisplayName("throws RestaurantNotFoundException when restaurant missing")
        void updateAvgRating_missing_throws() {
            when(restaurantRepository.existsById(OWNER_99)).thenReturn(false);

            assertThatThrownBy(() -> service.updateAvgRating(OWNER_99, 4.5))
                    .isInstanceOf(RestaurantNotFoundException.class);
        }
    }
}
