package control.azienda;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.AziendaDAO;
import model.dao.TrattaDAO;
import model.sdata.Tratta;
import model.udata.Azienda;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/prvAzienda/gestisciTratte")
public class TrattaManagementServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(TrattaManagementServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        Utente utente = (Utente) session.getAttribute("utente");
        
        try {
            // Recupera l'azienda dell'utente
            Azienda azienda = AziendaDAO.fromIDutente(utente.getId());
            if (azienda == null) {
                req.setAttribute("errore", "Azienda non trovata per l'utente corrente");
                req.getRequestDispatcher("/").forward(req, resp);
                return;
            }
            
            // Recupera le tratte dell'azienda (incluse quelle inattive)
            List<Tratta> tratteAzienda = TrattaDAO.getTratteByAziendaIncludingInactive(azienda.getId());
            
            // Passa i dati alla JSP
            req.setAttribute("azienda", azienda);
            req.setAttribute("tratte", tratteAzienda);
            req.setAttribute("totaleTratte", tratteAzienda.size());
            req.setAttribute("tratteAttive", tratteAzienda.stream()
                    .mapToInt(t -> t.getAttiva() ? 1 : 0)
                    .sum());
            
            req.getRequestDispatcher("/prvAzienda/gestisciTratte.jsp").forward(req, resp);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle tratte", e);
            req.setAttribute("errore", "Errore nel recupero delle tratte: " + e.getMessage());
            req.getRequestDispatcher("/").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        String action = req.getParameter("action");
        String trattaIdStr = req.getParameter("trattaId");
        
        if (action == null || trattaIdStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }
        
        try {
            Long trattaId = Long.parseLong(trattaIdStr);
            
            switch (action) {
                case "attiva":
                    TrattaDAO.setStatus(trattaId, true);
                    break;
                case "disattiva":
                    TrattaDAO.setStatus(trattaId, false);
                    break;
                case "toggle":
                    toggleTrattaStatus(trattaId);
                    break;
                case "delete":
                    deleteTratta(trattaId);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Azione non riconosciuta");
                    return;
            }
            
            // Verifica se è una richiesta AJAX
            String acceptHeader = req.getHeader("Accept");
            if (acceptHeader != null && acceptHeader.contains("application/json")) {
                // Risposta JSON per le richieste AJAX
                resp.setContentType("application/json");
                resp.getWriter().write("{\"success\": true}");
            } else {
                // Redirect per le richieste normali form
                resp.sendRedirect(req.getContextPath() + "/prvAzienda/gestisciTratte");
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID tratta non valido");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'operazione sulla tratta", e);
            
            String acceptHeader = req.getHeader("Accept");
            if (acceptHeader != null && acceptHeader.contains("application/json")) {
                resp.setContentType("application/json");
                resp.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
            } else {
                req.setAttribute("errore", "Errore nell'operazione: " + e.getMessage());
                doGet(req, resp); // Ricarica la pagina con l'errore
            }
        }
    }
    
    private void toggleTrattaStatus(Long trattaId) throws SQLException {
        Tratta tratta = TrattaDAO.getById(trattaId);
        if (tratta != null) {
            if (tratta.isAttiva()) {
                TrattaDAO.deativate(trattaId);
            } else {
                TrattaDAO.activate(trattaId);
            }
        }
    }
    
    private void deleteTratta(Long trattaId) throws SQLException {
        // Invece di eliminare fisicamente, disattiva la tratta
        TrattaDAO.deativate(trattaId);
    }
}
