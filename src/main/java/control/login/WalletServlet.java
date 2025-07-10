package control.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.BigliettiDAO; // Added import
import model.udata.Biglietto; // Added import
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException; // Added import
import java.util.List; // Added import

@WebServlet("/wallet")
public class WalletServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            // Utente non loggato, reindirizza alla pagina di login con redirectURL
            String redirectURL = req.getContextPath() + "/wallet";
            resp.sendRedirect(req.getContextPath() + "/login?redirectURL=" + redirectURL);
        } else {
            // Utente loggato, mostra la pagina del portafoglio
            try {
                List<Biglietto> biglietti = BigliettiDAO.findBigliettiByUtente(utente.getId());
                req.setAttribute("biglietti", biglietti);
            } catch (SQLException e) {
                System.err.println("Errore nel recupero dei biglietti per l'utente " + utente.getId() + ": " + e.getMessage());
                req.setAttribute("errorMessage", "Errore durante il caricamento dei biglietti.");
            }
            req.getRequestDispatcher("/prvUser/wallet.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso non autorizzato. Effettua il login.");
            return;
        }

        if ("activateTicket".equals(action)) {
            try {
                Long idBiglietto = Long.parseLong(req.getParameter("idBiglietto"));
                // Optional: Verify if the ticket belongs to the logged-in user for security
                Biglietto biglietto = BigliettiDAO.getById(idBiglietto);
                if (biglietto != null && biglietto.getIdUtente().equals(utente.getId())) {
                    BigliettiDAO.activateTicket(idBiglietto);
                    req.setAttribute("successMessage", "Biglietto attivato con successo!");
                } else {
                    req.setAttribute("errorMessage", "Biglietto non trovato o non appartenente all'utente.");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("errorMessage", "ID Biglietto non valido.");
            } catch (SQLException e) {
                System.err.println("Errore durante l'attivazione del biglietto: " + e.getMessage());
                req.setAttribute("errorMessage", "Errore del database durante l'attivazione del biglietto.");
            }
        } else {
            req.setAttribute("errorMessage", "Azione non riconosciuta.");
        }
        doGet(req, resp); // Reindirizza al doGet per ricaricare la pagina con i dati aggiornati e messaggi
    }
}
