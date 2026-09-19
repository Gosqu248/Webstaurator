-- Migrate all primary keys and foreign keys from BIGSERIAL/BIGINT to UUID.
-- Safe to apply on empty dev database (no production data to migrate).

-- Drop tables in reverse FK dependency order
DROP TABLE IF EXISTS opening_hours;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS restaurants;

-- Recreate with UUID primary keys

CREATE TABLE restaurants (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id          UUID         NOT NULL,
    name              VARCHAR(100) NOT NULL,
    description       TEXT,
    cuisine_type      VARCHAR(30)  NOT NULL,
    address           VARCHAR(200) NOT NULL,
    city              VARCHAR(100) NOT NULL,
    latitude          DOUBLE PRECISION,
    longitude         DOUBLE PRECISION,
    phone_number      VARCHAR(20),
    logo_url          VARCHAR(500),
    banner_url        VARCHAR(500),
    is_active         BOOLEAN      NOT NULL DEFAULT TRUE,
    avg_rating        DOUBLE PRECISION       DEFAULT 0.0,
    delivery_time_min INT,
    delivery_fee      NUMERIC(6,2),
    min_order_amount  NUMERIC(8,2),
    created_at        TIMESTAMP    NOT NULL  DEFAULT NOW()
);

CREATE INDEX idx_restaurants_city  ON restaurants (LOWER(city));
CREATE INDEX idx_restaurants_owner ON restaurants (owner_id);

CREATE TABLE categories (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID         NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    name          VARCHAR(60)  NOT NULL,
    display_order INT          NOT NULL DEFAULT 0,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_categories_restaurant ON categories (restaurant_id);

CREATE TABLE menu_items (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id          UUID         NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    restaurant_id        UUID         NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    name                 VARCHAR(200) NOT NULL,
    description          TEXT,
    price                NUMERIC(8,2) NOT NULL,
    image_url            VARCHAR(500),
    is_available         BOOLEAN      NOT NULL DEFAULT TRUE,
    preparation_time_min INT,
    calories             INT
);

CREATE INDEX idx_menu_items_category   ON menu_items (category_id);
CREATE INDEX idx_menu_items_restaurant ON menu_items (restaurant_id);

CREATE TABLE opening_hours (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID        NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    day_of_week   VARCHAR(3)  NOT NULL,
    open_time     TIME        NOT NULL,
    close_time    TIME        NOT NULL
);

CREATE INDEX idx_opening_hours_restaurant ON opening_hours (restaurant_id);
