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
import java.util.ArrayList;
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
            List<Biglietto> biglietti = BigliettiDAO.getAllUserActive(utente.getId());
            // Aggiorna lo stato dei biglietti (verifica scadenze)
            biglietti.forEach(Biglietto::verificaScadenza);
            
            // Carica i biglietti completi nella sessione per la visualizzazione
            session.setAttribute("bigliettiCompleti", biglietti);
            
            // Controlla se ci sono biglietti guest da reclamare
            @SuppressWarnings("unchecked")
            List<Biglietto> guestTickets = (List<Biglietto>) session.getAttribute("guestTickets");
            
            if (guestTickets != null && !guestTickets.isEmpty()) {
                req.setAttribute("guestTickets", guestTickets);
                req.setAttribute("showClaimButton", true);
            }
            
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
        
        if ("claimGuestTickets".equals(action)) {
            // Reclama i biglietti guest
            try {
                @SuppressWarnings("unchecked")
                List<Biglietto> guestTickets = (List<Biglietto>) session.getAttribute("guestTickets");
                
                if (guestTickets != null && !guestTickets.isEmpty()) {
                    Utente utente = (Utente) session.getAttribute("utente");
                    
                    // Estrai gli ID dei biglietti guest che hanno un ID nel database
                    List<Long> guestTicketIds = new ArrayList<>();
                    for (Biglietto biglietto : guestTickets) {
                        if (biglietto.getId() != null) {
                            guestTicketIds.add(biglietto.getId());
                        } else {
                            // Se il biglietto non ha un ID, crealo prima nel database
                            biglietto.setId_utente(-1L); // Temporaneamente assegnato a guest
                            
                            // Imposta valori di default per le liste se sono nulle
                            if (biglietto.getId_tratte() == null) {
                                biglietto.setId_tratte(new ArrayList<>());
                            }
                            if (biglietto.getNumero_fermate() == null) {
                                biglietto.setNumero_fermate(new ArrayList<>());
                            }
                            
                            Long bigliettoId = BigliettiDAO.create(biglietto);
                            if (bigliettoId != null) {
                                guestTicketIds.add(bigliettoId);
                            }
                        }
                    }
                    
                    // Usa la funzione del DAO per reclamare i biglietti
                    int claimedTickets = BigliettiDAO.claimGuestTickets(utente.getId(), guestTicketIds);
                    
                    // Svuota i biglietti guest dalla sessione
                    session.removeAttribute("guestTickets");
                    
                    if (claimedTickets > 0) {
                        req.setAttribute("success", "Hai reclamato " + claimedTickets + " biglietti!");
                    } else {
                        req.setAttribute("error", "Nessun biglietto è stato reclamato.");
                    }
                } else {
                    req.setAttribute("error", "Nessun biglietto guest da reclamare.");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("error", "Errore nel reclamare i biglietti: " + e.getMessage());
            }
            
        } else if ("activateTicket".equals(action)) {
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
