INSERT INTO orders (order_number, customer_name, item_name, quantity, total_amount, status, order_date,
                    estimated_delivery_date, tracking_number, carrier, current_location)
SELECT 'ORD-1001',
       'Alice Johnson',
       'Wireless Headphones',
       1,
       79.99,
       'SHIPPED',
       '2026-07-15',
       '2026-07-24',
       'TRK-9001',
       'FedEx',
       'Chicago, IL Distribution Center' WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-1001');

INSERT INTO orders (order_number, customer_name, item_name, quantity, total_amount, status, order_date,
                    estimated_delivery_date, tracking_number, carrier, current_location)
SELECT 'ORD-1023',
       'Bob Smith',
       'Mechanical Keyboard',
       1,
       129.50,
       'OUT_FOR_DELIVERY',
       '2026-07-17',
       '2026-07-22',
       'TRK-9023',
       'UPS',
       'Local Delivery Facility - Dublin' WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-1023');

INSERT INTO orders (order_number, customer_name, item_name, quantity, total_amount, status, order_date,
                    estimated_delivery_date, tracking_number, carrier, current_location)
SELECT 'ORD-1045',
       'Carla Diaz',
       '4K Monitor',
       1,
       349.00,
       'DELIVERED',
       '2026-07-10',
       '2026-07-14',
       'TRK-9045',
       'DHL',
       'Delivered - Front Door' WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-1045');

INSERT INTO orders (order_number, customer_name, item_name, quantity, total_amount, status, order_date,
                    estimated_delivery_date, tracking_number, carrier, current_location)
SELECT 'ORD-1050',
       'Bob Smith',
       'USB-C Hub',
       2,
       39.98,
       'PLACED',
       '2026-07-21',
       '2026-07-29',
       NULL,
       NULL,
       'Order Processing Center' WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-1050');

INSERT INTO orders (order_number, customer_name, item_name, quantity, total_amount, status, order_date,
                    estimated_delivery_date, tracking_number, carrier, current_location)
SELECT 'ORD-1099',
       'Diana Prince',
       'Standing Desk',
       1,
       459.00,
       'CANCELLED',
       '2026-07-05',
       NULL,
       NULL,
       NULL,
       NULL WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-1099');
