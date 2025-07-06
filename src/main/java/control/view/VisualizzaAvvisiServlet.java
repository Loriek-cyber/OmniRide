package control.view;

import error.ErrorPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.AvvisiDAO;
import model.sdata.Avvisi;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "visualizzaAvvisi", value = "/visualizzaAvvisi")
public class VisualizzaAvvisiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Avvisi> avvisi = AvvisiDAO.getAllAvvisi();
            req.setAttribute("avvisi", avvisi);
            req.getRequestDispatcher("/avvisi.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", new ErrorPage(500,"Errore con il Database"));
            req.getRequestDispatcher("/error").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
