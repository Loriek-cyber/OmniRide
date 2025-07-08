package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.TrattaDAO;
import model.dao.udata.SessioneDAO;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
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

        HttpSession httpSession = req.getSession();
        
        // Verifica e aggiorna la sessione personalizzata se presente
        String sessionId = (String) httpSession.getAttribute("sessionId");
        if (sessionId != null) {
            try {
                boolean sessioneValida = SessioneDAO.sessioneEsistente(sessionId);
                if (sessioneValida) {
                    SessioneDAO.aggiornaUltimoAccesso(sessionId, Instant.now().getEpochSecond());
                } else {
                    // Sessione non valida, reindirizza al login
                    httpSession.invalidate();
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
            } catch (SQLException e) {
                System.out.println("[CARRELLO ERROR] Errore durante la verifica della sessione: " + e.getMessage());
                // Procedi comunque per compatibilità
            }
        }
        
        Map<Long, Tratta> carrello = (Map<Long, Tratta>) httpSession.getAttribute("carrello");
        if (carrello == null) {
            carrello = new HashMap<>();
            httpSession.setAttribute("carrello", carrello);
        }

        try {
            if ("add".equals(action)) {
                long idTratta = Long.parseLong(req.getParameter("idTratta"));
                if (!carrello.containsKey(idTratta)) {
                    Tratta tratta = TrattaDAO.findById(idTratta);
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
