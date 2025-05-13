package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://mysql-omniride-1412-omniride.i.aivencloud.com:24112/defaultdb?ssl-mode=REQUIRED";
    private static final String DB_USER = "avnadmin";

    public static Connection getConnection() throws SQLException {
        // Retrieve the password from the system environment variable
        String dbPassword = System.getenv("DB_PASSWORD");
        

        if (dbPassword == null) {
        	throw new SQLException("Database password not found in environment variables.");
        }
        
        // Print Password for debugging (remove in production)
        
        
        
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found", e);
        }

        return DriverManager.getConnection(DB_URL, DB_USER, dbPassword);
    }
}
