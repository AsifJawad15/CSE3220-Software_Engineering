-- Admin user (password: admin123)
INSERT INTO users (name, email, password, role) VALUES
    ('Admin', 'admin@market.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN');
-- Regular users (password: password123)
INSERT INTO users (name, email, password, role) VALUES
    ('Alice', 'alice@market.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_USER'),
    ('Bob', 'bob@market.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_USER');
INSERT INTO user_profiles (user_id, phone, address) VALUES
    (1, '01700000001', 'Dhaka, Bangladesh'),
    (2, '01700000002', 'Chittagong, Bangladesh'),
    (3, '01700000003', 'Sylhet, Bangladesh');
INSERT INTO products (name, price, stock, description) VALUES
    ('Wireless Mouse', 25.99, 100, 'Ergonomic wireless mouse with USB receiver'),
    ('Mechanical Keyboard', 79.99, 50, 'RGB mechanical keyboard with Cherry MX switches'),
    ('USB-C Hub', 35.50, 75, '7-in-1 USB-C hub with HDMI, USB 3.0, SD card reader'),
    ('Monitor Stand', 45.00, 30, 'Adjustable aluminum monitor stand'),
    ('Webcam HD', 55.00, 60, '1080p HD webcam with built-in microphone');