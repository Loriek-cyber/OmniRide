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
        HttpSession session = req.getSession(false);

        // Se l'utente non è loggato, controlla se ha biglietti ospite nella sessione client-side
        if (session == null || session.getAttribute("utente") == null) {
            // Reindirizza alla pagina wallet ospite che gestirà i biglietti da sessionStorage
            req.getRequestDispatcher("/wallet.jsp").forward(req, resp);
            return;
        }

        // Se l'utente è loggato, carica i suoi biglietti dal database
        Utente utente = (Utente) session.getAttribute("utente");
        try {
            // Recupera tutti i biglietti dell'utente
            List<Biglietto> biglietti = BigliettiDAO.getAllUser(utente.getId());
            
            // Aggiorna lo stato dei biglietti (verifica scadenze)
            biglietti.forEach(Biglietto::verificaScadenza);
            
            // Carica i biglietti completi nella sessione per la visualizzazione
            session.setAttribute("bigliettiCompleti", biglietti);
            
            // Passa i biglietti alla JSP
            req.setAttribute("biglietti", biglietti);
            
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Errore nel caricamento dei biglietti: " + e.getMessage());
        }

        // Mostra la pagina del portafoglio per utenti registrati
        req.getRequestDispatcher("/prvUser/wallet.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String action = req.getParameter("action");
        
        if ("activateTicket".equals(action)) {
            String bigliettoIdStr = req.getParameter("idBiglietto");
            
            if (bigliettoIdStr != null) {
                try {
                    Long bigliettoId = Long.parseLong(bigliettoIdStr);
                    Biglietto biglietto = BigliettiDAO.getbyId(bigliettoId);
                    
                    if (biglietto != null) {
                        // Verifica che il biglietto appartenga all'utente
                        Utente utente = (Utente) session.getAttribute("utente");
                        if (biglietto.getId_utente().equals(utente.getId())) {
                            // Attiva il biglietto
                            if (biglietto.attiva()) {
                                // Aggiorna nel database
                                BigliettiDAO.update(biglietto);
                                req.setAttribute("success", "Biglietto attivato con successo!");
                            } else {
                                req.setAttribute("error", "Impossibile attivare il biglietto. Potrebbe essere già attivo o scaduto.");
                            }
                        } else {
                            req.setAttribute("error", "Non autorizzato ad attivare questo biglietto.");
                        }
                    } else {
                        req.setAttribute("error", "Biglietto non trovato.");
                    }
                    
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "ID biglietto non valido.");
                } catch (Exception e) {
                    e.printStackTrace();
                    req.setAttribute("error", "Errore durante l'attivazione del biglietto: " + e.getMessage());
                }
            }
        }
        
        // Reindirizza alla pagina GET per ricaricare i biglietti
        resp.sendRedirect(req.getContextPath() + "/wallet");
    }
}
