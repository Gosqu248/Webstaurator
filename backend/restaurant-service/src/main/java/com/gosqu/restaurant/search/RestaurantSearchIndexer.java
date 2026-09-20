package com.gosqu.restaurant.search;

import com.gosqu.restaurant.menu.MenuItem;
import com.gosqu.restaurant.menu.MenuItemRepository;
import com.gosqu.restaurant.restaurant.Restaurant;
import com.gosqu.restaurant.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Materializuje RestaurantDocument z Postgresa do Elasticsearch. Zawsze przelicza cały
 * dokument od zera (restauracja + wszystkie jej dania), zamiast robić partial update —
 * przy evencie na pojedyncze danie i tak trzeba znać resztę dań żeby złożyć poprawny
 * dokument nested, a pełny reindex jednej restauracji jest tu tani i eliminuje ryzyko
 * rozjazdu stanu przy równoległych eventach na tę samą restaurację.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantSearchIndexer {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantSearchRepository restaurantSearchRepository;

    @Transactional(readOnly = true)
    public void reindexRestaurant(UUID restaurantId) {
        restaurantRepository.findById(restaurantId).ifPresentOrElse(
                restaurant -> {
                    RestaurantDocument document = toDocument(restaurant);
                    restaurantSearchRepository.save(document);
                    log.info("restaurant_indexed restaurantId={}", restaurantId);
                },
                () -> {
                    restaurantSearchRepository.deleteById(restaurantId.toString());
                    log.info("restaurant_removed_from_index restaurantId={}", restaurantId);
                }
        );
    }

    @Transactional(readOnly = true)
    public void reindexAll() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<RestaurantDocument> documents = restaurants.stream().map(this::toDocument).toList();
        restaurantSearchRepository.saveAll(documents);
        log.info("restaurant_reindex_all count={}", documents.size());
    }

    private RestaurantDocument toDocument(Restaurant restaurant) {
        List<MenuItemSummary> menuItems = menuItemRepository.findAllByRestaurantId(restaurant.getId())
                .stream()
                .map(this::toSummary)
                .toList();

        GeoPoint location = (restaurant.getLatitude() != null && restaurant.getLongitude() != null)
                ? new GeoPoint(restaurant.getLatitude(), restaurant.getLongitude())
                : null;

        return new RestaurantDocument(
                restaurant.getId().toString(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getCuisineType().name(),
                restaurant.getCity(),
                location,
                restaurant.getAvgRating(),
                restaurant.getIsActive(),
                menuItems
        );
    }

    private MenuItemSummary toSummary(MenuItem item) {
        return new MenuItemSummary(
                item.getId().toString(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable()
        );
    }
}
