INSERT INTO orders (order_number, customer_name, item_name, quantity, total_amount, status,
                     order_date, estimated_delivery_date, tracking_number, carrier,
                     current_location, delivery_address, cancellation_reason)
VALUES
    ('ORD-1001', 'Alice Johnson', 'Wireless Noise-Cancelling Headphones', 1, 249.99, 'SHIPPED',
     '2026-08-05', '2026-08-14', 'FDX-8842019321', 'FedEx',
     'Distribution Center, Newark, NJ', '221B Baker Street, London, UK', NULL),

    ('ORD-1002', 'Brian Smith', 'Mechanical Keyboard', 2, 189.98, 'OUT_FOR_DELIVERY',
     '2026-08-08', '2026-08-14', 'UPS-773410982', 'UPS',
     'Out for delivery near Dublin, IE', '14 Grafton Street, Dublin, IE', NULL),

    ('ORD-1003', 'Carla Mendes', '27-inch 4K Monitor', 1, 429.50, 'PROCESSING',
     '2026-08-12', '2026-08-19', NULL, NULL,
     NULL, 'Rua das Flores 88, Lisbon, PT', NULL),

    ('ORD-1004', 'David Chen', 'USB-C Docking Station', 3, 314.97, 'DELIVERED',
     '2026-07-28', '2026-08-03', 'DHL-559102734', 'DHL',
     'Delivered', '55 Orchard Road, Singapore', NULL),

    ('ORD-1005', 'Alice Johnson', 'Ergonomic Office Chair', 1, 359.00, 'CANCELLED',
     '2026-08-01', NULL, NULL, NULL,
     NULL, '221B Baker Street, London, UK', 'Customer requested cancellation - found local alternative'),

    ('ORD-1006', 'Emeka Okafor', 'Smart Home Hub', 1, 99.99, 'CONFIRMED',
     '2026-08-13', '2026-08-20', NULL, NULL,
     NULL, '12 Marina Bay Ave, Lagos, NG', NULL);
