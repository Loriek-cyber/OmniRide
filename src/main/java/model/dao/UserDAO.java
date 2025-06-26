package model.dao;

import model.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/omniride";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // SQL Queries
    private static final String INSERT_USER = 
        "INSERT INTO users (email, password, first_name, last_name, address, city, postal_code, phone, is_admin, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_USER_BY_ID = 
        "SELECT * FROM users WHERE id = ?";
    
    private static final String SELECT_USER_BY_EMAIL = 
        "SELECT * FROM users WHERE email = ?";
    
    private static final String SELECT_ALL_USERS = 
        "SELECT * FROM users ORDER BY created_at DESC";
    
    private static final String UPDATE_USER = 
        "UPDATE users SET email = ?, first_name = ?, last_name = ?, address = ?, city = ?, postal_code = ?, phone = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE_USER = 
        "DELETE FROM users WHERE id = ?";
    
    private static final String AUTHENTICATE_USER = 
        "SELECT * FROM users WHERE email = ? AND password = ?";
    
    // Get database connection
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
    
    // Create a new user
    public boolean createUser(User user) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getCity());
            stmt.setString(7, user.getPostalCode());
            stmt.setString(8, user.getPhone());
            stmt.setBoolean(9, user.isAdmin());
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    // Get user by ID
    public User getUserById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_ID)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    // Get user by email
    public User getUserByEmail(String email) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_EMAIL)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    // Authenticate user
    public User authenticate(String email, String password) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(AUTHENTICATE_USER)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password); // In production, use hashed passwords
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    // Get all users
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    // Update user
    public boolean updateUser(User user) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USER)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getCity());
            stmt.setString(6, user.getPostalCode());
            stmt.setString(7, user.getPhone());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(9, user.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    // Delete user
    public boolean deleteUser(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    // Check if email exists
    public boolean emailExists(String email) throws SQLException {
        return getUserByEmail(email) != null;
    }
    
    // Helper method to map ResultSet to User object
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setAddress(rs.getString("address"));
        user.setCity(rs.getString("city"));
        user.setPostalCode(rs.getString("postal_code"));
        user.setPhone(rs.getString("phone"));
        user.setAdmin(rs.getBoolean("is_admin"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
}