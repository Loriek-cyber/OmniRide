package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.TrattaDAO;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Azione non specificata.");
            return;
        }

        HttpSession session = req.getSession();
        Map<Long, Tratta> carrello = (Map<Long, Tratta>) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new HashMap<>();
            session.setAttribute("carrello", carrello);
        }

        try {
            if ("add".equals(action)) {
                long idTratta = Long.parseLong(req.getParameter("idTratta"));
                if (!carrello.containsKey(idTratta)) {
                    Tratta tratta = TrattaDAO.getTrattaByID(idTratta);
                    if (tratta != null) {
                        carrello.put(idTratta, tratta);
                    }
                }
            } else if ("remove".equals(action)) {
                long idTratta = Long.parseLong(req.getParameter("idTratta"));
                carrello.remove(idTratta);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID tratta non valido.");
            return;
        } catch (SQLException e) {
            throw new ServletException("Errore database nel carrello", e);
        }

        // Reindirizza alla pagina del carrello dopo ogni azione
        resp.sendRedirect(req.getContextPath() + "/carrello.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Il GET mostra semplicemente la pagina del carrello
        req.getRequestDispatcher("/carrello.jsp").forward(req, resp);
    }
}
