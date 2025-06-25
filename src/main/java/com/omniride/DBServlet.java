package com.omniride;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import net.db.DBConnector;

@WebServlet(name = "DBServlet", value = "/DBServlet")
public class DBServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            connection = DBConnector.getConnection();
        } catch (SQLException e) {
            throw new ServletException("Impossibile inizializzare la connessione al database", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Verifica se la connessione è ancora valida
            if (connection.isClosed()) {
                connection = DBConnector.getConnection();
            }
            
            // Utilizza la connessione salvata
            // Esempio: PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tabella");
            
        } catch (SQLException e) {
            throw new ServletException("Errore durante l'operazione database", e);
        }
    }

    @Override
    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Log dell'errore ma non lanciare eccezione durante destroy
                e.printStackTrace();
            }
        }
    }
}