package control.test;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.sdata.*;

import model.db.DBConnector;


@WebServlet(name = "DBtestServlet", value = "/DBtestServlet")
public class DBtestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String start = (String) request.getParameter("start");
        String end = (String) request.getParameter("end");

        String sql = "SELECT * FROM Utente";
        ArrayList<User> content = null;
        try {
            Connection conn = null;
            try {
                conn = DBConnector.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (DBConnector.isConnected(conn)) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    try (ResultSet rs = stmt.executeQuery()) {
                        ArrayList<User> users = null;
                        while (rs.next()) {
                            users = new ArrayList<>();
                            users.add(new User(rs.getInt("id"), rs.getString("nome"), rs.getString("cognome")));

                        }

                        content = users;
                        request.setAttribute("content", content);
                    }
                } catch (SQLException e) {
                    request.setAttribute("content", "invalid");
                    throw new RuntimeException(e);
                }


                RequestDispatcher ris = request.getRequestDispatcher("result.jsp");

                ris.forward(request, response);
            }
        } finally {

        }
    }}
