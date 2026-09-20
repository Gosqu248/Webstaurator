package com.gosqu.restaurant.search;

import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public RestaurantSearchResult search(String city, String cuisineType, String q,
                                          Double lat, Double lon, Double radiusKm,
                                          Pageable pageable) {
        Query query = Query.of(root -> root.bool(bool -> {
            bool.filter(f -> f.term(t -> t.field("isActive").value(true)));

            if (city != null) {
                bool.filter(f -> f.term(t -> t.field("city").value(city)));
            }
            if (cuisineType != null) {
                bool.filter(f -> f.term(t -> t.field("cuisineType").value(cuisineType)));
            }
            if (q != null && !q.isBlank()) {
                bool.should(s -> s.multiMatch(m -> m.fields("name^3", "description").query(q)));
                bool.should(s -> s.nested(n -> n
                        .path("menuItems")
                        .scoreMode(ChildScoreMode.Max)
                        .query(nq -> nq.match(m -> m.field("menuItems.name").query(q)))));
                bool.minimumShouldMatch("1");
            }
            if (lat != null && lon != null && radiusKm != null) {
                bool.filter(f -> f.geoDistance(g -> g
                        .field("location")
                        .distance(radiusKm + "km")
                        .location(loc -> loc.latlon(ll -> ll.lat(lat).lon(lon)))));
            }

            return bool;
        }));

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .build();

        SearchHits<RestaurantDocument> hits = elasticsearchOperations.search(nativeQuery, RestaurantDocument.class);

        List<UUID> orderedIds = hits.getSearchHits().stream()
                .map(hit -> UUID.fromString(hit.getContent().id()))
                .toList();

        return new RestaurantSearchResult(orderedIds, hits.getTotalHits());
    }
}
