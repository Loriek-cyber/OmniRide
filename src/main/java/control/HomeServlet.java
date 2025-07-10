package control;

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

@WebServlet(name = "HomeServlet", value = "/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Avvisi> avvisi = AvvisiDAO.getAllAvvisi();
            request.setAttribute("avvisi", avvisi);
        } catch (SQLException e) {
            // Logga l'errore o gestiscilo in modo appropriato
            System.err.println("Errore nel recupero degli avvisi: " + e.getMessage());
            // Potresti voler impostare un attributo di errore per la JSP
        }
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
