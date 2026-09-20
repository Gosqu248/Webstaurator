package com.gosqu.restaurant.restaurant;

import com.gosqu.restaurant.restaurant.dto.response.RestaurantOwnerResponse;
import com.gosqu.restaurant.search.RestaurantSearchIndexer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/restaurants")
@RequiredArgsConstructor
class InternalRestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantSearchIndexer restaurantSearchIndexer;

    @GetMapping("/{id}/owner")
    public RestaurantOwnerResponse getOwner(@PathVariable UUID id) {
        return new RestaurantOwnerResponse(restaurantService.getOwnerId(id));
    }

    // Ręczny backfill indeksu ES — wywołać raz po pierwszym uruchomieniu z Elasticsearch
    // (restauracje z danych seedowych nie mają jeszcze dokumentu, bo powstał zanim
    // istniał ten indeks) oraz w razie potrzeby odtworzenia indeksu od zera.
    @PostMapping("/reindex")
    public ResponseEntity<Void> reindexAll() {
        restaurantSearchIndexer.reindexAll();
        return ResponseEntity.accepted().build();
    }
}
