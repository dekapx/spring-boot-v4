CREATE TABLE IF NOT EXISTS products (
    id                BIGSERIAL PRIMARY KEY,
    sku               VARCHAR(32)     NOT NULL UNIQUE,
    name              VARCHAR(255)    NOT NULL,
    description       TEXT            NOT NULL,
    category          VARCHAR(64)     NOT NULL,
    brand             VARCHAR(64),
    price             NUMERIC(12, 2)  NOT NULL,
    stock_quantity    INTEGER         NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_products_category ON products (category);
