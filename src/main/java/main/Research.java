package main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

import utils.DBConnector;

@WebServlet("/Research")
public class Research extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Research() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (Connection conn = DBConnector.getConnection()) {
            response.getWriter().append("Servito su: ").append(request.getContextPath());
            response.getWriter().append("<br>Connessione al DB: ");
            if (conn != null) {
                response.getWriter().append("<h1>Connesso</h1>");
            } else {
                response.getWriter().append("<h1>Non connesso</h1>");
            }
        } catch (SQLException e) {
            PrintWriter out = response.getWriter();
            out.append("<h1>Errore durante la connessione al DB:</h1>");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            out.append("<pre>").append(sw.toString()).append("</pre>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}