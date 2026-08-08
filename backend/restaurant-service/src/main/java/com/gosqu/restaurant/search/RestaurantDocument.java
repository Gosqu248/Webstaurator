package com.gosqu.restaurant.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

/**
 * Read-model dla wyszukiwania — Postgres (Restaurant/MenuItem) zostaje źródłem prawdy,
 * ten dokument jest aktualizowany asynchronicznie przez RestaurantIndexConsumer.
 * Celowo nie duplikuje pól niepotrzebnych do filtrowania/scoringu (adres, telefon, logo...)
 * — po dopasowaniu w ES restauracje są dociągane z Postgresa (patrz RestaurantSearchService).
 */
@Document(indexName = "restaurants")
public record RestaurantDocument(
        @Id String id,
        @Field(type = FieldType.Text) String name,
        @Field(type = FieldType.Text) String description,
        @Field(type = FieldType.Keyword) String cuisineType,
        @Field(type = FieldType.Keyword) String city,
        @GeoPointField GeoPoint location,
        @Field(type = FieldType.Double) Double avgRating,
        @Field(type = FieldType.Boolean) Boolean isActive,
        @Field(type = FieldType.Nested, includeInParent = true) List<MenuItemSummary> menuItems
) {
}
