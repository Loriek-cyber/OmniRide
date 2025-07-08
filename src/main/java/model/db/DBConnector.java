package model.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/*
* Connection to the private docker server
* */

public class DBConnector {
    // Parametri di accesso
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://13.49.243.124:3306/omniride";
    private static final String USER = "rootomni";
    private static final String PASS = "omniridepass";

    public static Connection getConnection() throws SQLException {
        try{
            // Carica il driver JDBC
            Class.forName(JDBC_DRIVER);
            // Apre la connessione al database
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC non trovato");
            throw new RuntimeException(e);
        }
        catch (SQLException e) {
            throw new RuntimeException("Non sono riuscito a connettermi");
        }
    }

    public static boolean isConnected(Connection conn) {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

}
