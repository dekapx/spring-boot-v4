CREATE TABLE IF NOT EXISTS orders (
    id                        BIGSERIAL PRIMARY KEY,
    order_number              VARCHAR(32)     NOT NULL UNIQUE,
    customer_name             VARCHAR(255)    NOT NULL,
    item_name                 VARCHAR(255)    NOT NULL,
    quantity                  INTEGER         NOT NULL,
    total_amount              NUMERIC(12, 2)  NOT NULL,
    status                    VARCHAR(32)     NOT NULL,
    order_date                DATE            NOT NULL,
    estimated_delivery_date   DATE,
    tracking_number           VARCHAR(64),
    carrier                   VARCHAR(64),
    current_location          VARCHAR(255),
    delivery_address          TEXT            NOT NULL,
    cancellation_reason       VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_orders_customer_name ON orders (customer_name);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders (status);
