package com.gosqu.restaurant.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RestaurantSearchRepository extends ElasticsearchRepository<RestaurantDocument, String> {
}
