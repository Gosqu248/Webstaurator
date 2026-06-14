CREATE INDEX idx_restaurant_search
    ON restaurants (is_active, cuisine_type, LOWER(city));

CREATE INDEX idx_restaurants_name_fts
    ON restaurants USING gin(to_tsvector('simple', name));
