CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    name        VARCHAR(60)  NOT NULL,
    password    VARCHAR(255),
    role        VARCHAR(50)  NOT NULL DEFAULT 'CUSTOMER',
    google_id   VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_users_email ON users (email);
