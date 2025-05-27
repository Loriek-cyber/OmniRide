package main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import utils.DBConnector;
import data.Tratta;
import data.Fermata;

@WebServlet("/Research")
public class Research extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Research() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Ricerca Tratta</title></head><body>");
            out.println("<h1>Dettagli della Tratta</h1>");

            // Creazione di alcune fermate di esempio
            Fermata fermata1 = new Fermata("F1", "Via Roma", null, 41.9028, 12.4964);
            Fermata fermata2 = new Fermata("F2", "Via Milano", null, 45.4642, 9.1900);
            Fermata fermata3 = new Fermata("F3", "Via Napoli", null, 40.8518, 14.2681);

            // Creazione della tratta e aggiunta delle fermate
            Tratta tratta = new Tratta("T1", new ArrayList<>());
            tratta.addFermata(fermata1);
            tratta.addFermata(fermata2);
            tratta.addFermata(fermata3);

            out.println("<p><strong>ID Tratta:</strong> " + tratta.getId() + "</p>");
            out.println("<ul>");
            out.println("<li>" + tratta.toString() + "</li>");
            out.println("</ul>");
            
            // Test della connessione al database
            try (Connection conn = DBConnector.getConnection()) {
                if (conn != null) {
                    out.println("<p>Connessione al database riuscita.</p>");
                } else {
                    out.println("<p>Connessione al database fallita.</p>");
                }
            } catch (SQLException e) {
                out.println("<p>Errore durante la connessione al database: " + e.getMessage() + "</p>");
            }
            
            out.println("</body></html>");
        } catch (Exception e) {
            throw new ServletException("Errore nella servlet Research", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}