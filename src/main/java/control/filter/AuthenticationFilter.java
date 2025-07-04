package control.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.udata.SessioneDAO;
import model.udata.Sessione;
import model.udata.Utente;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/prvUser/*", "/prvAdmin/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession httpSession = httpRequest.getSession(false);

        Utente utente = null;
        boolean isLoggedIn = false;
        String requestURI = httpRequest.getRequestURI();
        
        if (httpSession != null) {
            // Controllo prima l'utente nella sessione HTTP
            utente = (Utente) httpSession.getAttribute("utente");
            String sessionId = (String) httpSession.getAttribute("sessionId");
            
            if (utente != null && sessionId != null) {
                // Verifica se la sessione è ancora valida nel database
                try {
                    boolean sessioneValida = SessioneDAO.sessioneEsistente(sessionId);
                    if (sessioneValida) {
                        // Aggiorna l'ultimo accesso
                        SessioneDAO.aggiornaUltimoAccesso(sessionId, Instant.now().getEpochSecond());
                        isLoggedIn = true;
                    } else {
                        // Sessione non valida nel database, invalida anche la sessione HTTP
                        httpSession.invalidate();
                        utente = null;
                    }
                } catch (SQLException e) {
                    System.out.println("[FILTER ERROR] Errore durante la verifica della sessione: " + e.getMessage());
                    // In caso di errore, mantengo la sessione HTTP per compatibilità
                    isLoggedIn = (utente != null);
                }
            }
        }

        // Se la richiesta è per l'area admin
        if (requestURI.startsWith(httpRequest.getContextPath() + "/prvAdmin/")) {
            if (isLoggedIn && "admin".equals(utente.getRuolo())) {
                // Utente loggato e ruolo admin: accesso consentito
                chain.doFilter(request, response);
            } else {
                // Utente non loggato o non admin: accesso negato
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso non autorizzato.");
            }
        }
        // Se la richiesta è per l'area utente generica
        else if (requestURI.startsWith(httpRequest.getContextPath() + "/prvUser/")) {
            if (isLoggedIn) {
                // Utente loggato: accesso consentito
                chain.doFilter(request, response);
            } else {
                // Utente non loggato: reindirizza al login
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            }
        }
        // Per tutte le altre richieste, non fare nulla (lascia passare)
        else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inizializzazione del filtro (opzionale)
    }

    @Override
    public void destroy() {
        // Pulizia del filtro (opzionale)
    }
}
