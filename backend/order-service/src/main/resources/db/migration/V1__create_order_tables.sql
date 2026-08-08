CREATE TABLE orders (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id          UUID NOT NULL,
    restaurant_id        UUID NOT NULL,
    restaurant_name      VARCHAR(200) NOT NULL,

    delivery_street      VARCHAR(200) NOT NULL,
    delivery_city        VARCHAR(100) NOT NULL,
    delivery_postal_code VARCHAR(10),
    delivery_country     VARCHAR(60)  NOT NULL DEFAULT 'Poland',
    delivery_latitude    DOUBLE PRECISION,
    delivery_longitude   DOUBLE PRECISION,

    delivery_fee_amount  NUMERIC(10,2) NOT NULL,
    delivery_fee_currency VARCHAR(3)   NOT NULL DEFAULT 'PLN',
    total_amount         NUMERIC(10,2) NOT NULL,
    total_currency       VARCHAR(3)    NOT NULL DEFAULT 'PLN',

    status               VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    cancellation_reason  TEXT,

    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP
);

CREATE TABLE order_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    menu_item_id        UUID NOT NULL,
    name                VARCHAR(200) NOT NULL,
    unit_price_amount   NUMERIC(10,2) NOT NULL,
    unit_price_currency VARCHAR(3)    NOT NULL DEFAULT 'PLN',
    quantity            INT           NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_orders_customer_id    ON orders (customer_id);
CREATE INDEX idx_orders_restaurant_id  ON orders (restaurant_id);
CREATE INDEX idx_orders_status         ON orders (status);
CREATE INDEX idx_order_items_order_id  ON order_items (order_id);
