package com.gosqu.restaurant.search;

import java.util.List;
import java.util.UUID;

/**
 * Wynik zapytania ES: same identyfikatory w kolejności trafności + liczba wszystkich
 * dopasowań (do paginacji). Świadomie bez danych restauracji — te dociąga RestaurantService
 * z Postgresa, ES tu pełni tylko rolę silnika wyszukiwania/rankingu, nie źródła danych.
 */
public record RestaurantSearchResult(List<UUID> orderedIds, long totalHits) {
}
