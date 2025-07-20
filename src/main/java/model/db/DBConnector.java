package model.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Connection to the private docker server
 */

public class DBConnector {
    // Parametri di accesso
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/omniride";
    private static final String USER = "server";
    private static String PASS = null;  // inizialmente null

    public static Connection getConnection() throws SQLException {
        try {
            // Carica il driver JDBC
            Class.forName(JDBC_DRIVER);

            // Recupera la password dall'ambiente se non è stata ancora impostata
            if (PASS == null) {
                PASS = System.getenv("DB_Password");
                if (PASS == null) {
                    throw new SQLException("La variabile d'ambiente DB_Password non è stata impostata");
                }
            }

            // Apro la connessione al database
            return DriverManager.getConnection(DB_URL, USER, PASS);

        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC non trovato");
            throw new SQLException("Driver JDBC non disponibile", e);
        } catch (SQLException e) {
            System.out.println("Errore di connessione al database: " + e.getMessage());
            throw e; // Rilancia l'eccezione SQL originale
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