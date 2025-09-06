-- DDL Script for Practice301

-- Drop tables if exist
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

-- Create table orders
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    order_date TIMESTAMP NOT NULL,
    order_email VARCHAR(200) NOT NULL,
    order_total_amount NUMERIC(15,2),
    order_total_qty INT
);

-- Create table order_items
CREATE TABLE order_items (
    detail_id UUID PRIMARY KEY,
    item_code VARCHAR(10) NOT NULL,
    item_qty INT NOT NULL,
    item_price NUMERIC(15,2),
    order_id UUID,
    CONSTRAINT fk_order FOREIGN KEY(order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- Sample data for orders
INSERT INTO orders (order_id, order_date, order_email, order_total_amount, order_total_qty)
VALUES 
('11111111-1111-1111-1111-111111111111', '2025-09-07 10:00:00', 'alice@example.com', 1200.00, 3),
('22222222-2222-2222-2222-222222222222', '2025-09-07 11:30:00', 'bob@example.com', 750.00, 2);

-- Sample data for order_items
INSERT INTO order_items (detail_id, item_code, item_qty, item_price, order_id)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'ITEM001', 2, 500.00, '11111111-1111-1111-1111-111111111111'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'ITEM002', 1, 200.00, '11111111-1111-1111-1111-111111111111'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'ITEM003', 2, 375.00, '22222222-2222-2222-2222-222222222222');
