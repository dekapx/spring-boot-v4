INSERT INTO orders (order_number, customer_name, item_name, quantity, total_amount, status,
                     order_date, estimated_delivery_date, tracking_number, carrier,
                     current_location, delivery_address, cancellation_reason)
SELECT 'ORD12345', 'Alice Walsh', 'Wireless Headphones', 1, 89.99, 'IN_TRANSIT',
       DATE '2026-08-01', DATE '2026-08-12', 'TRK998877', 'DHL',
       'Dublin Sorting Facility', '14 Grafton Street, Dublin, Ireland', NULL
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD12345');

INSERT INTO orders (order_number, customer_name, item_name, quantity, total_amount, status,
                     order_date, estimated_delivery_date, tracking_number, carrier,
                     current_location, delivery_address, cancellation_reason)
SELECT 'ORD67890', 'Brian O''Connor', 'Mechanical Keyboard', 1, 129.50, 'DELIVERED',
       DATE '2026-07-20', DATE '2026-07-28', 'TRK112233', 'FedEx',
       'Delivered to front door', '5 Patrick Street, Cork, Ireland', NULL
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD67890');

INSERT INTO orders (order_number, customer_name, item_name, quantity, total_amount, status,
                     order_date, estimated_delivery_date, tracking_number, carrier,
                     current_location, delivery_address, cancellation_reason)
SELECT 'ORD24680', 'Ciara Byrne', '4K Monitor', 2, 610.00, 'PROCESSING',
       DATE '2026-08-08', DATE '2026-08-18', NULL, 'An Post',
       'Warehouse - Dublin', '9 Merrion Square, Dublin, Ireland', NULL
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD24680');
