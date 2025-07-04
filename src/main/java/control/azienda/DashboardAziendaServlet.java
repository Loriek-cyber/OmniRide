package control.azienda;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.udata.SessioneDAO;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;

/**
 * Servlet per gestire la dashboard delle aziende.
 * Fornisce accesso alle funzionalità di gestione aziendale per gli utenti con ruolo 'azienda'.
 * 
 * Funzionalità principali:
 * - Controllo autorizzazioni (solo utenti azienda)
 * - Gestione sessioni
 * - Reindirizzamento alla dashboard appropriata
 */
@WebServlet(name = "dashboardAzienda", value = "/prvAzienda/dashboard")
public class DashboardAziendaServlet extends HttpServlet {
    
    /**
     * Gestisce le richieste GET per visualizzare la dashboard azienda.
     * Verifica l'autenticazione e l'autorizzazione dell'utente prima di consentire l'accesso.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Ottieni la sessione HTTP (non creare se non esiste)
        HttpSession httpSession = request.getSession(false);
        
        // Controllo autenticazione di base
        if (httpSession == null || httpSession.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Ottieni l'utente dalla sessione
        Utente utente = (Utente) httpSession.getAttribute("utente");
        String sessionId = (String) httpSession.getAttribute("sessionId");
        
        // Controllo autorizzazione: solo utenti azienda possono accedere
        if (!"azienda".equals(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/prvUser/dashboard");
            return;
        }
        
        // Verifica e aggiorna la sessione personalizzata nel database
        if (sessionId != null) {
            try {
                boolean sessioneValida = SessioneDAO.sessioneEsistente(sessionId);
                if (sessioneValida) {
                    // Aggiorna il timestamp dell'ultimo accesso
                    SessioneDAO.aggiornaUltimoAccesso(sessionId, Instant.now().getEpochSecond());
                } else {
                    // Sessione non valida nel database, invalida anche quella HTTP
                    httpSession.invalidate();
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
            } catch (SQLException e) {
                System.err.println("[DASHBOARD AZIENDA ERROR] Errore durante la verifica della sessione: " + e.getMessage());
                // Procedi comunque per compatibilità, ma logga l'errore
            }
        }

        // Imposta attributi per la JSP (se necessario per statistiche future)
        request.setAttribute("nomeAzienda", utente.getNome() + " " + utente.getCognome());
        
        // TODO: In futuro, qui si potrebbero caricare statistiche specifiche dell'azienda
        // come numero di tratte, fermate gestite, ricavi, etc.
        
        // Forward alla pagina della dashboard azienda
        RequestDispatcher dispatcher = request.getRequestDispatcher("/prvAzienda/dashboardAzienda.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Gestisce le richieste POST (attualmente non implementate).
     * Potrebbero essere utilizzate in futuro per aggiornamenti delle impostazioni azienda.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Per ora reindirizza alle richieste GET
        doGet(request, response);
    }
}
