package net.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnector {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/omniride?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root"; // <-- SOSTITUISCI con il tuo username
    private static final String DB_PASSWORD = "password"; // <-- SOSTITUISCI con la tua password
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private DBConnector() {}
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trovato.", e);
        }

        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }
}