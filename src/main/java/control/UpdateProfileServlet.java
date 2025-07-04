package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.UtenteDAO;
import model.dao.udata.SessioneDAO;
import model.udata.Utente;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;

@WebServlet("/updateProfile")
public class UpdateProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession httpSession = req.getSession(false);
        if (httpSession == null || httpSession.getAttribute("utente") == null) {
            // Utente non loggato, reindirizza al login
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Utente utenteInSessione = (Utente) httpSession.getAttribute("utente");
        String sessionId = (String) httpSession.getAttribute("sessionId");
        
        // Verifica e aggiorna la sessione personalizzata se presente
        if (sessionId != null) {
            try {
                boolean sessioneValida = SessioneDAO.sessioneEsistente(sessionId);
                if (sessioneValida) {
                    SessioneDAO.aggiornaUltimoAccesso(sessionId, Instant.now().getEpochSecond());
                } else {
                    // Sessione non valida, reindirizza al login
                    httpSession.invalidate();
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
            } catch (SQLException e) {
                System.out.println("[UPDATE_PROFILE ERROR] Errore durante la verifica della sessione: " + e.getMessage());
                // Procedi comunque per compatibilità
            }
        }

        String nome = req.getParameter("nome");
        String cognome = req.getParameter("cognome");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        // Validazione di base
        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Tutti i campi obbligatori (Nome, Cognome, Email) devono essere compilati.");
            req.getRequestDispatcher("/prvUser/dashboard.jsp").forward(req, resp);
            return;
        }

        // Validazione email: controlla se è cambiata e se la nuova email è già in uso
        if (!email.equals(utenteInSessione.getEmail())) {
            try {
                if (UtenteDAO.findByEmail(email) != null) {
                    req.setAttribute("errorMessage", "Questa email è già associata ad un altro account.");
                    req.getRequestDispatcher("/prvUser/dashboard.jsp").forward(req, resp);
                    return;
                }
            } catch (SQLException e) {
                throw new ServletException("Errore database durante la validazione dell'email.", e);
            }
        }

        // Validazione password
        if (password != null && !password.isEmpty()) {
            if (!password.equals(confirmPassword)) {
                req.setAttribute("errorMessage", "Le password non corrispondono.");
                req.getRequestDispatcher("/prvUser/dashboard.jsp").forward(req, resp);
                return;
            }
            // Hash della nuova password
            utenteInSessione.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        }

        // Aggiorna i dati dell'utente
        utenteInSessione.setNome(nome);
        utenteInSessione.setCognome(cognome);
        utenteInSessione.setEmail(email);

        try {
            boolean success = UtenteDAO.update(utenteInSessione); // Questo metodo dovrà essere aggiunto a UtenteDAO

            if (success) {
                // Aggiorna l'utente nella sessione con i dati più recenti
                httpSession.setAttribute("utente", utenteInSessione);
                req.setAttribute("successMessage", "Profilo aggiornato con successo!");
            } else {
                req.setAttribute("errorMessage", "Errore durante l'aggiornamento del profilo. Riprova.");
            }
        } catch (SQLException e) {
            throw new ServletException("Errore database durante l'aggiornamento del profilo.", e);
        }

        req.getRequestDispatcher("/prvUser/dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Reindirizza a doPost o mostra la pagina di modifica (se si volesse un form pre-compilato al primo accesso)
        doPost(req, resp);
    }
}
