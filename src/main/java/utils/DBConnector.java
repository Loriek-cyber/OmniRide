package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnector {

    private static final Logger LOGGER = Logger.getLogger(DBConnector.class.getName());
    private static final String DB_URL = "jdbc:mysql://mysql-omniride-1412-omniride.i.aivencloud.com:24112/defaultdb?ssl-mode=REQUIRED";
    private static final String DB_USER = "avnadmin";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("JDBC Driver è stato caricato con successo.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "JDBC Driver non trovato", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbPassword == null || dbPassword.isEmpty()) {
            LOGGER.severe("La variabile ambientale DB_PASSWORD non è stata trovata, inserirla o contattare Arjel.");
            throw new SQLException("Database password not set.");
        }

        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, dbPassword);
        LOGGER.fine("Connessione riuscita con successo.");
        return conn;
    }
}
