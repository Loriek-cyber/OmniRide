package control.login;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dao.AvvisiDAO;
import model.dao.udata.SessioneDAO;
import model.sdata.Avvisi;
import model.udata.Sessione;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;


/**
 * Servlet principale per la gestione della dashboard utenti.
 * Gestisce l'accesso alla dashboard in base al ruolo dell'utente:
 * - Utenti normali: dashboard standard
 * - Utenti azienda: reindirizzamento alla dashboard azienda
 * - Admin: dashboard admin (se configurata)
 */
@WebServlet(name = "dashboard", value = "/prvUser/dashboard")
public class DashboardServlet extends HttpServlet {
    
    /**
     * Gestisce le richieste GET per l'accesso alla dashboard.
     * Verifica l'autenticazione dell'utente e lo reindirizza alla dashboard appropriata.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Ottieni la sessione HTTP esistente (non creare se non esiste)
        HttpSession httpSession = request.getSession(false);
        
        // Controllo autenticazione di base
        if (httpSession == null || httpSession.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Ottieni dati utente dalla sessione
        Utente utente = (Utente) httpSession.getAttribute("utente");
        String sessionId = (String) httpSession.getAttribute("sessionId");
        
        // Reindirizzamento in base al ruolo utente
        if ("azienda".equals(utente.getRuolo())) {
            // Gli utenti azienda vengono reindirizzati alla loro dashboard specifica
            response.sendRedirect(request.getContextPath() + "/prvAzienda/dashboard");
            return;
        }
        
        // Verifica e aggiorna la sessione personalizzata
        if (sessionId != null) {
            try {
                boolean sessioneValida = SessioneDAO.sessioneEsistente(sessionId);
                if (sessioneValida) {
                    SessioneDAO.aggiornaUltimoAccesso(sessionId, Instant.now().getEpochSecond());
                } else {
                    // Sessione non valida, reindirizza al login
                    httpSession.invalidate();
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
            } catch (SQLException e) {
                System.out.println("[DASHBOARD ERROR] Errore durante la verifica della sessione: " + e.getMessage());
                // Procedi comunque per compatibilità
            }
        }
        try {
            List<Avvisi> avvisiList = AvvisiDAO.getAllAvvisi();
            request.setAttribute("avvisi",avvisiList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/prvUser/dashboard.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
