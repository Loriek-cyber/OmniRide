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

        // Se l'utente non è loggato, gestisci i biglietti guest tramite ID dai cookie
        if (session == null || session.getAttribute("utente") == null) {
            // Recupera gli ID dei biglietti guest dai cookie
            List<Long> guestTicketIds = new ArrayList<>();
            
            jakarta.servlet.http.Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("guestTicketIds".equals(cookie.getName())) {
                        String encodedValue = cookie.getValue();
                        if (encodedValue != null && !encodedValue.isEmpty()) {
                            try {
                                // Decodifica il valore del cookie
                                String idsString = java.net.URLDecoder.decode(encodedValue, "UTF-8");
                                
                                // Usa | come separatore invece di ,
                                String[] idStrings = idsString.split("\\|");
                                for (String idStr : idStrings) {
                                    try {
                                        guestTicketIds.add(Long.parseLong(idStr.trim()));
                                    } catch (NumberFormatException e) {
                                        System.err.println("ID biglietto non valido nel cookie: " + idStr);
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Errore nella decodifica del cookie: " + e.getMessage());
                            }
                        }
                        break;
                    }
                }
            }
            
            List<Biglietto> guestBiglietti = new ArrayList<>();
            
            if (!guestTicketIds.isEmpty()) {
                System.out.println("[WALLET DEBUG] Recuperando " + guestTicketIds.size() + " biglietti guest dal database (cookie)");
                
                // Usa il nuovo metodo per recuperare biglietti attivi tramite ID
                try {
                    guestBiglietti = BigliettiDAO.getActiveTicketsByIds(guestTicketIds);
                    System.out.println("[WALLET DEBUG] Trovati " + guestBiglietti.size() + " biglietti attivi");
                    
                    // Se alcuni biglietti sono scaduti o non esistono più, aggiorna il cookie
                    if (guestBiglietti.size() < guestTicketIds.size()) {
                        List<Long> validIds = guestBiglietti.stream()
                            .map(Biglietto::getId)
                            .collect(java.util.stream.Collectors.toList());
                        
                        String updatedIdsString = validIds.stream()
                            .map(String::valueOf)
                            .collect(java.util.stream.Collectors.joining("|"));  // Usa | invece di ,
                        
                        try {
                            // Codifica il valore del cookie aggiornato
                            String encodedValue = java.net.URLEncoder.encode(updatedIdsString, "UTF-8");
                            
                            jakarta.servlet.http.Cookie updatedCookie = new jakarta.servlet.http.Cookie("guestTicketIds", encodedValue);
                            updatedCookie.setMaxAge(30 * 24 * 60 * 60); // 30 giorni
                            updatedCookie.setPath("/");
                            updatedCookie.setHttpOnly(true);
                            resp.addCookie(updatedCookie);
                            
                            System.out.println("[WALLET DEBUG] Cookie aggiornato con " + validIds.size() + " ID validi");
                        } catch (Exception e) {
                            System.err.println("Errore nella codifica del cookie aggiornato: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel recupero dei biglietti guest: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Passa i biglietti alla JSP
            req.setAttribute("biglietti", guestBiglietti);
            req.setAttribute("isGuest", true);
            
            System.out.println("[WALLET DEBUG] Passando " + guestBiglietti.size() + " biglietti alla JSP guest");
            
            // Reindirizza alla pagina wallet guest con i biglietti dal database
            req.getRequestDispatcher("/wallet.jsp").forward(req, resp);
            return;
        }

        // Se l'utente è loggato, carica i suoi biglietti dal database
        Utente utente = (Utente) session.getAttribute("utente");
        try {
            // Recupera tutti i biglietti attivi dell'utente (già filtrati dalla query)
            List<Biglietto> biglietti = BigliettiDAO.getAllUserActive(utente.getId());
            
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
            
        } else if ("associateGuestTicket".equals(action)) {
            // Associa un biglietto guest all'utente corrente
            String guestTicketId = req.getParameter("guestTicketId");
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            
            if (guestTicketId == null || guestTicketId.trim().isEmpty()) {
                resp.getWriter().write("{\"success\": false, \"message\": \"ID biglietto non fornito\"}");
                return;
            }
            
            try {
                Utente utente = (Utente) session.getAttribute("utente");
                
                // Cerca il biglietto guest nel database
                Biglietto guestTicket = BigliettiDAO.findGuestTicketByCode(guestTicketId.trim());
                
                if (guestTicket == null) {
                    resp.getWriter().write("{\"success\": false, \"message\": \"Biglietto non trovato o già associato\"}");
                    return;
                }
                
                // Verifica che il biglietto non sia già associato a un utente
                if (guestTicket.getId_utente() != null && guestTicket.getId_utente() > 0) {
                    resp.getWriter().write("{\"success\": false, \"message\": \"Questo biglietto è già associato a un account\"}");
                    return;
                }
                
                // Associa il biglietto all'utente corrente
                boolean success = BigliettiDAO.associateGuestTicketToUser(guestTicket.getId(), utente.getId());
                
                if (success) {
                    resp.getWriter().write("{\"success\": true, \"message\": \"Biglietto associato con successo\"}");
                } else {
                    resp.getWriter().write("{\"success\": false, \"message\": \"Errore nell'associazione del biglietto\"}");
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                resp.getWriter().write("{\"success\": false, \"message\": \"Errore del database: \" + e.getMessage()}");
            } catch (Exception e) {
                e.printStackTrace();
                resp.getWriter().write("{\"success\": false, \"message\": \"Errore interno del server\"}");
            }
            return;
            
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
                            // Verifica che il biglietto possa essere attivato
                            if (biglietto.getStato() == Biglietto.StatoBiglietto.ACQUISTATO) {
                                // Attiva il biglietto
                                if (biglietto.attiva()) {
                                    // Aggiorna nel database
                                    BigliettiDAO.update(biglietto);
                                    session.setAttribute("successMessage", "Biglietto attivato con successo! Ora è valido e il countdown è iniziato.");
                                } else {
                                    session.setAttribute("errorMessage", "Errore tecnico nell'attivazione del biglietto.");
                                }
                            } else if (biglietto.getStato() == Biglietto.StatoBiglietto.CONVALIDATO) {
                                session.setAttribute("errorMessage", "Questo biglietto è già attivo.");
                            } else {
                                session.setAttribute("errorMessage", "Questo biglietto non può essere attivato (stato: " + biglietto.getStato() + ")");
                            }
                        } else {
                            session.setAttribute("errorMessage", "Non autorizzato ad attivare questo biglietto.");
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
