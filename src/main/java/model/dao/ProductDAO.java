package model.dao;

import model.Product;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/omniride";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // SQL Queries
    private static final String INSERT_PRODUCT = 
        "INSERT INTO products (name, description, price, stock_quantity, category, image_url, active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_PRODUCT_BY_ID = 
        "SELECT * FROM products WHERE id = ?";
    
    private static final String SELECT_ALL_PRODUCTS = 
        "SELECT * FROM products ORDER BY created_at DESC";
    
    private static final String SELECT_ACTIVE_PRODUCTS = 
        "SELECT * FROM products WHERE active = true ORDER BY created_at DESC";
    
    private static final String SELECT_PRODUCTS_BY_CATEGORY = 
        "SELECT * FROM products WHERE category = ? AND active = true ORDER BY name";
    
    private static final String UPDATE_PRODUCT = 
        "UPDATE products SET name = ?, description = ?, price = ?, stock_quantity = ?, category = ?, image_url = ?, active = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE_PRODUCT = 
        "DELETE FROM products WHERE id = ?";
    
    private static final String DEACTIVATE_PRODUCT = 
        "UPDATE products SET active = false, updated_at = ? WHERE id = ?";
    
    private static final String UPDATE_STOCK = 
        "UPDATE products SET stock_quantity = ?, updated_at = ? WHERE id = ?";
    
    private static final String SEARCH_PRODUCTS = 
        "SELECT * FROM products WHERE (name LIKE ? OR description LIKE ?) AND active = true ORDER BY name";
    
    // Get database connection
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
    
    // Create a new product
    public boolean createProduct(Product product) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStockQuantity());
            stmt.setString(5, product.getCategory());
            stmt.setString(6, product.getImageUrl());
            stmt.setBoolean(7, product.isActive());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    // Get product by ID
    public Product getProductById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUCT_BY_ID)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }
    
    // Get all products (for admin)
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PRODUCTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }
    
    // Get active products (for customers)
    public List<Product> getActiveProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACTIVE_PRODUCTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }
    
    // Get products by category
    public List<Product> getProductsByCategory(String category) throws SQLException {
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUCTS_BY_CATEGORY)) {
            
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }
    
    // Search products
    public List<Product> searchProducts(String searchTerm) throws SQLException {
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_PRODUCTS)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }
    
    // Update product
    public boolean updateProduct(Product product) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PRODUCT)) {
            
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStockQuantity());
            stmt.setString(5, product.getCategory());
            stmt.setString(6, product.getImageUrl());
            stmt.setBoolean(7, product.isActive());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(9, product.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    // Delete product (hard delete)
    public boolean deleteProduct(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_PRODUCT)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    // Deactivate product (soft delete)
    public boolean deactivateProduct(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DEACTIVATE_PRODUCT)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    // Update stock quantity
    public boolean updateStock(int productId, int newQuantity) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_STOCK)) {
            
            stmt.setInt(1, newQuantity);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, productId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    // Decrease stock (for orders)
    public boolean decreaseStock(int productId, int quantity) throws SQLException {
        Product product = getProductById(productId);
        if (product != null && product.getStockQuantity() >= quantity) {
            int newQuantity = product.getStockQuantity() - quantity;
            return updateStock(productId, newQuantity);
        }
        return false;
    }
    
    // Get distinct categories
    public List<String> getCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String query = "SELECT DISTINCT category FROM products WHERE active = true ORDER BY category";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }
    
    // Helper method to map ResultSet to Product object
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setCategory(rs.getString("category"));
        product.setImageUrl(rs.getString("image_url"));
        product.setActive(rs.getBoolean("active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return product;
    }
}