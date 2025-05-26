package main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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

        response.getWriter().append("Served at: ").append(request.getContextPath());
        response.getWriter().append("<br>DB Connection: ");

        try {
            if (DBConnector.getConnection()) {
                response.getWriter().append("<h1>Connected</h1>");
            } else {
                response.getWriter().append("<h1>Not connected</h1>");
            }
        } catch (IOException | SQLException e) {
            response.getWriter().append("<h1>Error while trying to connect to the DB</h1>");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}