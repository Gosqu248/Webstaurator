CREATE TABLE user_profiles (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL UNIQUE,
    first_name   VARCHAR(60),
    last_name    VARCHAR(60),
    phone_number VARCHAR(20),
    avatar_url   VARCHAR(500),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE addresses (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL,
    label        VARCHAR(30),
    street       VARCHAR(200) NOT NULL,
    city         VARCHAR(100) NOT NULL,
    postal_code  VARCHAR(10),
    country      VARCHAR(60) NOT NULL DEFAULT 'Poland',
    latitude     DOUBLE PRECISION,
    longitude    DOUBLE PRECISION,
    is_default   BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_addresses_user_id ON addresses (user_id);
