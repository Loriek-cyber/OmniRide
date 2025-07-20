package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.Carta_CreditoDAO;
import model.udata.CartaCredito;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;


@WebServlet(name="card",value = "/prvUser/card")
public class CardServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Se l'utente non è loggato, controlla se ha biglietti ospite nella sessione client-side
        if (session == null || session.getAttribute("utente") == null) {
            // Reindirizza alla pagina wallet ospite che gestirà i biglietti da sessionStorage
            request.getRequestDispatcher("/login").forward(request, response);
        }
        Utente ut =  (Utente) session.getAttribute("utente");

        try {
            var carteCredito = Carta_CreditoDAO.byIDUtente(ut.getId());
            request.setAttribute("carteCredito", carteCredito);
        } catch (SQLException e) {
            response.sendError(500, "Errore nel recupero delle carte: " + e.getMessage());
            return;
        }

        request.getRequestDispatcher("/prvUser/creditCards.jsp").forward(request, response);
    }
}
