package control.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.udata.Utente;
import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/prvUser/*", "/prvAdmin/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;
        boolean isLoggedIn = (utente != null);
        String requestURI = httpRequest.getRequestURI();

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
