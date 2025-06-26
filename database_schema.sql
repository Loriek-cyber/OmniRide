-- Omniride E-commerce Database Schema
-- MySQL Database Schema for E-commerce Application

-- Create database
CREATE DATABASE IF NOT EXISTS omniride;
USE omniride;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    phone VARCHAR(20),
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_is_admin (is_admin)
);

-- Products table
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category VARCHAR(100) NOT NULL,
    image_url VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_active (active),
    INDEX idx_name (name),
    INDEX idx_price (price)
);

-- Orders table
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    shipping_city VARCHAR(100) NOT NULL,
    shipping_postal_code VARCHAR(20) NOT NULL,
    payment_method VARCHAR(50),
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    shipped_date TIMESTAMP NULL,
    delivered_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date),
    INDEX idx_payment_status (payment_status)
);

-- Order items table
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
);

-- Shopping cart table (for persistent cart storage)
CREATE TABLE cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_product_id (product_id),
    UNIQUE KEY unique_user_product (user_id, product_id),
    UNIQUE KEY unique_session_product (session_id, product_id)
);

-- Product categories table (for better category management)
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_active (active)
);

-- Insert default admin user (password should be hashed in production)
INSERT INTO users (email, password, first_name, last_name, is_admin) 
VALUES ('admin@omniride.com', 'admin123', 'Admin', 'User', TRUE);

-- Insert sample categories
INSERT INTO categories (name, description) VALUES 
('Trasporti', 'Servizi di trasporto pubblico e privato'),
('Biglietti', 'Biglietti per eventi e trasporti'),
('Abbonamenti', 'Abbonamenti mensili e annuali'),
('Servizi Premium', 'Servizi di trasporto premium');

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category, active) VALUES 
('Biglietto Singolo Urbano', 'Biglietto per una corsa singola nel trasporto urbano', 1.50, 1000, 'Biglietti', TRUE),
('Abbonamento Mensile', 'Abbonamento mensile per trasporto urbano illimitato', 35.00, 100, 'Abbonamenti', TRUE),
('Biglietto Intercity', 'Biglietto per trasporto intercity', 15.00, 200, 'Biglietti', TRUE),
('Servizio Premium Airport', 'Trasporto premium da/per aeroporto', 45.00, 50, 'Servizi Premium', TRUE),
('Abbonamento Annuale', 'Abbonamento annuale per trasporto urbano illimitato', 350.00, 50, 'Abbonamenti', TRUE);

-- Create indexes for better performance
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date);
CREATE INDEX idx_order_items_order_product ON order_items(order_id, product_id);
CREATE INDEX idx_products_category_active ON products(category, active);

-- Create views for common queries
CREATE VIEW active_products AS
SELECT * FROM products WHERE active = TRUE;

CREATE VIEW order_summary AS
SELECT 
    o.id,
    o.user_id,
    u.email,
    u.first_name,
    u.last_name,
    o.status,
    o.total_amount,
    o.order_date,
    o.payment_status
FROM orders o
JOIN users u ON o.user_id = u.id;

CREATE VIEW product_sales AS
SELECT 
    p.id,
    p.name,
    p.category,
    COALESCE(SUM(oi.quantity), 0) as total_sold,
    COALESCE(SUM(oi.total_price), 0) as total_revenue
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.id AND o.status != 'CANCELLED'
GROUP BY p.id, p.name, p.category;

-- Triggers for maintaining data integrity
DELIMITER //

CREATE TRIGGER update_order_total 
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    UPDATE orders 
    SET total_amount = (
        SELECT SUM(total_price) 
        FROM order_items 
        WHERE order_id = NEW.order_id
    )
    WHERE id = NEW.order_id;
END//

CREATE TRIGGER update_order_total_on_update
AFTER UPDATE ON order_items
FOR EACH ROW
BEGIN
    UPDATE orders 
    SET total_amount = (
        SELECT SUM(total_price) 
        FROM order_items 
        WHERE order_id = NEW.order_id
    )
    WHERE id = NEW.order_id;
END//

CREATE TRIGGER update_order_total_on_delete
AFTER DELETE ON order_items
FOR EACH ROW
BEGIN
    UPDATE orders 
    SET total_amount = (
        SELECT COALESCE(SUM(total_price), 0) 
        FROM order_items 
        WHERE order_id = OLD.order_id
    )
    WHERE id = OLD.order_id;
END//

DELIMITER ;

-- Grant permissions (adjust as needed for your environment)
-- GRANT ALL PRIVILEGES ON omniride.* TO 'omniride_user'@'localhost' IDENTIFIED BY 'your_password';
-- FLUSH PRIVILEGES;