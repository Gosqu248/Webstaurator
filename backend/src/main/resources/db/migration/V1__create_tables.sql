CREATE TABLE payments (
                         id BIGSERIAL PRIMARY KEY,
                         method VARCHAR(255) NOT NULL,
                         image TEXT
);

CREATE TABLE restaurants (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             category VARCHAR(255) NOT NULL,
                             logo_url TEXT,
                             image_url TEXT
);

CREATE TABLE menus (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      category TEXT NOT NULL,
                      ingredients TEXT,
                      price DECIMAL(10, 2) NOT NULL,
                      image TEXT
);

CREATE TABLE restaurant_addresses (
                                      id BIGSERIAL PRIMARY KEY,
                                      street VARCHAR(255) NOT NULL,
                                      flat_number VARCHAR(255) NOT NULL,
                                      city VARCHAR(255) NOT NULL,
                                      zip_code VARCHAR(255) NOT NULL,
                                      latitude DOUBLE PRECISION NOT NULL,
                                      longitude DOUBLE PRECISION NOT NULL,
                                      restaurant_id BIGINT,
                                      CONSTRAINT fk_restaurant_address FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE deliveries (
                          id BIGSERIAL PRIMARY KEY,
                          delivery_min_time INTEGER NOT NULL,
                          delivery_max_time INTEGER NOT NULL,
                          delivery_price DECIMAL(10, 2) NOT NULL,
                          minimum_price DECIMAL(10, 2) NOT NULL,
                          pickup_time INTEGER,
                          restaurant_id BIGINT,
                          CONSTRAINT fk_delivery_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE delivery_hours (
                               id BIGSERIAL PRIMARY KEY,
                               day_of_week INTEGER NOT NULL,
                               open_time VARCHAR(255),
                               close_time VARCHAR(255),
                               restaurant_id BIGINT,
                               CONSTRAINT fk_delivery_hour_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE additives (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           value VARCHAR(255) NOT NULL,
                           price DECIMAL(10, 2)
);

CREATE TABLE restaurant_menus (
                                 restaurant_id BIGINT NOT NULL,
                                 menu_id BIGINT NOT NULL,
                                 PRIMARY KEY (restaurant_id, menu_id),
                                 CONSTRAINT fk_restaurant_menu_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
                                 CONSTRAINT fk_restaurant_menu_menu FOREIGN KEY (menu_id) REFERENCES menus (id)
);

CREATE TABLE restaurant_payments (
                                    restaurant_id BIGINT NOT NULL,
                                    payment_id BIGINT NOT NULL,
                                    PRIMARY KEY (restaurant_id, payment_id),
                                    CONSTRAINT fk_restaurant_payment_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
                                    CONSTRAINT fk_restaurant_payment_payment FOREIGN KEY (payment_id) REFERENCES payments (id)
);

CREATE TABLE menu_additives (
                                menu_id BIGINT NOT NULL,
                                additive_id BIGINT NOT NULL,
                                PRIMARY KEY (menu_id, additive_id),
                                CONSTRAINT fk_menu_additives_menu FOREIGN KEY (menu_id) REFERENCES menus (id),
                                CONSTRAINT fk_menu_additives_additives FOREIGN KEY (additive_id) REFERENCES additives(id)
);

CREATE TABLE restaurant_opinions (
                                    id BIGSERIAL PRIMARY KEY,
                                    restaurant_id BIGINT,
                                    CONSTRAINT fk_restaurant_opinion_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE favourite_restaurants (
                                      id BIGSERIAL PRIMARY KEY,
                                      restaurant_id BIGINT,
                                      CONSTRAINT fk_favourite_restaurant_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        restaurant_id BIGINT,
                        CONSTRAINT fk_order_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE order_menus (
                            id BIGSERIAL PRIMARY KEY,
                            menu_id BIGINT,
                            CONSTRAINT fk_order_menu_menu FOREIGN KEY (menu_id) REFERENCES menus (id)
);

CREATE TABLE order_menu_additives (
                                      order_menu_id BIGINT NOT NULL,
                                      additive_id BIGINT NOT NULL,
                                      PRIMARY KEY (order_menu_id, additive_id),
                                      CONSTRAINT fk_order_menu_additives_order_menu FOREIGN KEY (order_menu_id) REFERENCES order_menus (id),
                                      CONSTRAINT fk_order_menu_additives_additives FOREIGN KEY (additive_id) REFERENCES additives(id)
);

CREATE TABLE address_suggestions (
                            id BIGSERIAL PRIMARY KEY,
                            lat DOUBLE PRECISION NOT NULL,
                            lon DOUBLE PRECISION NOT NULL,
                            name VARCHAR(255) NOT NULL
);
