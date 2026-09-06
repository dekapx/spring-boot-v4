INSERT INTO orders (
    carrier, current_location, customer_name, estimated_delivery_date,
    item_name, order_date, order_number, quantity, status,
    total_amount, tracking_number, delivery_address, cancellation_reason
) VALUES
      ('FedEx', 'Memphis, TN', 'John Smith', '2026-08-05',
       'Wireless Mouse', '2026-07-28', 'ORD-1001', 2, 'SHIPPED',
       49.98, 'FX123456789US', '123 Main St, Springfield, IL 62701', NULL),

      ('UPS', 'Louisville, KY', 'Emily Johnson', '2026-08-03',
       'Mechanical Keyboard', '2026-07-27', 'ORD-1002', 1, 'IN_TRANSIT',
       89.99, '1Z999AA10123456784', '456 Oak Ave, Denver, CO 80203', NULL),

      ('DHL', 'Dublin, Ireland', 'Aoife Byrne', '2026-08-01',
       '27-inch Monitor', '2026-07-25', 'ORD-1003', 1, 'DELIVERED',
       259.50, 'DHL7654321IE', '78 Grafton Street, Dublin 2, Ireland', NULL),

      ('USPS', NULL, 'Michael Brown', NULL,
       'Bluetooth Speaker', '2026-07-20', 'ORD-1004', 3, 'CANCELLED',
       119.97, NULL, '789 Pine Rd, Austin, TX 78701', 'Customer requested cancellation'),

      ('FedEx', 'Chicago, IL', 'Sarah Davis', '2026-08-10',
       'Laptop Stand', '2026-07-30', 'ORD-1005', 1, 'PENDING',
       34.99, NULL, '321 Elm St, Boston, MA 02108', NULL),

      ('UPS', 'Newark, NJ', 'David Wilson', '2026-08-07',
       'USB-C Hub', '2026-07-29', 'ORD-1006', 5, 'PROCESSING',
       149.95, 'Z1234567890123456', '654 Maple Dr, Seattle, WA 98101', NULL);