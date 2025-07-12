package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.BigliettiDAO;
import model.udata.Biglietto;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/wallet")
public class WalletServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true); // Crea una sessione se non esiste
        
        Utente utente = (Utente) session.getAttribute("utente");
        
        if (utente != null) {
            // Utente loggato: recupera i biglietti dal database
            try {
                List<Biglietto> biglietti = BigliettiDAO.GetBigliettoFromUserID(utente.getId());
                req.setAttribute("biglietti", biglietti);
                req.getRequestDispatcher("/prvUser/wallet.jsp").forward(req, resp);
            } catch (SQLException e) {
                // Gestione errore database
                req.setAttribute("errorMessage", "Errore nel recupero dei biglietti: " + e.getMessage());
                req.getRequestDispatcher("/prvUser/wallet.jsp").forward(req, resp);
            }
        } else {
            // Utente non loggato: controlla se ha biglietti in sessione
            Object bigliettoSessione = session.getAttribute("biglietto");
            req.setAttribute("bigliettoSessione", bigliettoSessione);
            req.getRequestDispatcher("/wallet.jsp").forward(req, resp); // Pagina wallet generale
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String action = req.getParameter("action");
        
        if ("activateTicket".equals(action)) {
            // Attivazione biglietto per utenti loggati
            String idBigliettoStr = req.getParameter("idBiglietto");
            
            if (idBigliettoStr != null) {
                try {
                    Long idBiglietto = Long.parseLong(idBigliettoStr);
                    
                    // Convalida il biglietto
                    boolean success = BigliettiDAO.convalidaBiglietto(idBiglietto);
                    
                    if (success) {
                        req.setAttribute("successMessage", "Biglietto attivato con successo!");
                    } else {
                        req.setAttribute("errorMessage", "Errore nell'attivazione del biglietto.");
                    }
                } catch (NumberFormatException e) {
                    req.setAttribute("errorMessage", "ID biglietto non valido.");
                } catch (SQLException e) {
                    req.setAttribute("errorMessage", "Errore database: " + e.getMessage());
                }
            }
        }
        
        // Redirect al GET per evitare risubmissione
        resp.sendRedirect(req.getContextPath() + "/wallet");
    }
}
