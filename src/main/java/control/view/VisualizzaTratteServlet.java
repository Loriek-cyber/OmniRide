package control.view;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.TrattaDAO;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "visualizzaTratte", value = "/visualizzaTratte")
public class VisualizzaTratteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("tratte", TrattaDAO.getAll());
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Errore con il Database");
            req.setAttribute("errorCode", 500);
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        } finally {
            req.getRequestDispatcher("tratte.jsp").forward(req, resp);
        }
    }
}
