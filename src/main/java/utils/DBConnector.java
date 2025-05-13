package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private static final String DB_URL = "jdbc:mysql://mysql-omniride-1412-omniride.i.aivencloud.com:24112/defaultdb?ssl-mode=REQUIRED";
    private static final String DB_USER = "avnadmin";
    private static final String DB_PASSWORD = "AVNS_gdOiYGNkxzpdQpoIiko";

    public static Connection getConnection() throws SQLException {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
