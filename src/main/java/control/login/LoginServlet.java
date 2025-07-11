package control.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.UtenteDAO;
import model.dao.udata.SessioneDAO;
import model.udata.Sessione;
import model.udata.Utente;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Se l'utente è già loggato, lo mando alla dashboard
        HttpSession session = req.getSession(false); // Non creare una nuova sessione se non esiste
        if (session != null && session.getAttribute("utente") != null) {
            resp.sendRedirect(req.getContextPath() + "/prvUser/dashboard.jsp");
            return;
        }
        // Altrimenti, mostro la pagina di login
        req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        email = email.toLowerCase();
        // Debug logging
        System.out.println("[LOGIN DEBUG] Tentativo di login per email: " + email);
        System.out.println("[LOGIN DEBUG] Password ricevuta: " + (password != null ? "[PRESENTE]" : "[ASSENTE]"));

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Email e password sono obbligatori.");
            req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
            return;
        }

        Utente utente = null;
        
        try {
            utente = UtenteDAO.findByEmail(email.trim());
        } catch (Exception e) {
            System.out.println("[LOGIN ERROR] Errore durante la ricerca dell'utente: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("errorMessage", "Errore del sistema. Riprova più tardi.");
            req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
            return;
        }

        if (utente == null) {
            System.out.println("[LOGIN DEBUG] Utente non trovato per email: " + email);
            req.setAttribute("errorMessage", "Email o password non validi. Riprova.");
            req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
            return;
        }

        System.out.println("[LOGIN DEBUG] Utente trovato: " + utente.getNome() + " " + utente.getCognome());
        System.out.println("[LOGIN DEBUG] Password hash dal DB: " + (utente.getPasswordHash() != null ? "[PRESENTE]" : "[ASSENTE]"));

        boolean passwordCorretta = false;
        String passwordHash = utente.getPasswordHash();

        if (passwordHash != null && !passwordHash.trim().isEmpty()) {
            // Verifica se la password è già hashata con BCrypt
            if (passwordHash.startsWith("$2a$") || passwordHash.startsWith("$2b$") || passwordHash.startsWith("$2y$")) {
                // Password hashata con BCrypt
                System.out.println("[LOGIN DEBUG] Verifica password con BCrypt...");
                try {
                    passwordCorretta = BCrypt.checkpw(password, passwordHash);
                    System.out.println("[LOGIN DEBUG] Risultato BCrypt: " + passwordCorretta);
                } catch (Exception e) {
                    System.out.println("[LOGIN ERROR] Errore durante la verifica BCrypt: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // Password in chiaro (per compatibilità)
                System.out.println("[LOGIN DEBUG] Verifica password in chiaro...");
                passwordCorretta = password.equals(passwordHash);
                System.out.println("[LOGIN DEBUG] Risultato confronto diretto: " + passwordCorretta);

                // Se la password è corretta ma in chiaro, aggiorniamo l'hash
                if (passwordCorretta) {
                    System.out.println("[LOGIN DEBUG] Aggiornamento hash password...");
                    try {
                        String nuovoHash = BCrypt.hashpw(password, BCrypt.gensalt());
                        utente.setPasswordHash(nuovoHash);
                        UtenteDAO.update(utente);
                        System.out.println("[LOGIN DEBUG] Hash password aggiornato");
                    } catch (Exception e) {
                        System.out.println("[LOGIN WARNING] Impossibile aggiornare l'hash: " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("[LOGIN DEBUG] Password hash è null o vuoto");
        }

        if (passwordCorretta) {
            // Login successo: creo la sessione e salvo l'utente
            System.out.println("[LOGIN SUCCESS] Login riuscito per: " + email);
            HttpSession httpSession = req.getSession(true);
            
            // Creo una nuova sessione personalizzata
            Sessione sessione = new Sessione();
            sessione.setUtente(utente);
            sessione.setValid(true);
            sessione.setCreationTime(Instant.now().getEpochSecond());
            sessione.setLastAccessTime(Instant.now().getEpochSecond());
            
            try {
                // Salvo la sessione nel database
                SessioneDAO.salvaSessione(sessione);
                
                // Salvo sia l'utente che la sessione nella HttpSession
                httpSession.setAttribute("utente", utente);
                httpSession.setAttribute("sessionId", sessione.getSessionId());
                httpSession.setAttribute("sessione", sessione);
                
                System.out.println("[SESSION DEBUG] Sessione creata con ID: " + sessione.getSessionId());
                
            } catch (SQLException e) {
                System.out.println("[SESSION ERROR] Errore durante il salvataggio della sessione: " + e.getMessage());
                System.out.println("[SESSION ERROR] Dettagli errore: " + e.toString());
                e.printStackTrace();
                
                // Anche se c'è un errore nel salvataggio della sessione personalizzata,
                // procedo con la sessione HTTP standard
                httpSession.setAttribute("utente", utente);
                // Non salvo sessionId se c'è stato un errore
                System.out.println("[SESSION WARNING] Continuo con sessione HTTP standard senza persistenza database");
            } catch (Exception e) {
                System.out.println("[SESSION ERROR] Errore generico durante la creazione della sessione: " + e.getMessage());
                e.printStackTrace();
                
                // Fallback a sessione HTTP standard
                httpSession.setAttribute("utente", utente);
                System.out.println("[SESSION WARNING] Fallback a sessione HTTP standard");
            }
            
            // Gestione del reindirizzamento
            String redirectURL = req.getParameter("redirectURL");
            if (redirectURL != null && !redirectURL.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + redirectURL);
            } else {
                resp.sendRedirect(req.getContextPath() + "/prvUser/dashboard.jsp");
            }
        } else {
            // Login fallito
            System.out.println("[LOGIN FAILED] Login fallito per: " + email);
            req.setAttribute("errorMessage", "Email o password non validi. Riprova.");
            req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
        }
    }
}
