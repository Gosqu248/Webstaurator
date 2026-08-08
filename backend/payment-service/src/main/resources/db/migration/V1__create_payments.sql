CREATE TABLE payments (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id                UUID NOT NULL,
    customer_id             UUID NOT NULL,
    amount                  NUMERIC(10,2) NOT NULL,
    currency                VARCHAR(3)    NOT NULL DEFAULT 'PLN',
    status                  VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    provider                VARCHAR(10)   NOT NULL,
    provider_transaction_id VARCHAR(255),
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP
);

CREATE INDEX idx_payments_order_id    ON payments (order_id);
CREATE INDEX idx_payments_customer_id ON payments (customer_id);
CREATE INDEX idx_payments_status      ON payments (status);
