package control.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.AziendaDAO;
import model.dao.UtenteDAO;
import model.udata.Azienda;
import model.udata.Utente;
import model.utils.ValidationUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

@WebServlet("/registerAzienda")
public class RegisterAziendaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Dati Azienda
        String nomeAzienda = req.getParameter("nomeAzienda");
        String tipoAziendaStr = req.getParameter("tipoAzienda");

        // Dati Utente
        String nome = req.getParameter("nome");
        String cognome = req.getParameter("cognome");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Normalizzazione e validazione input usando ValidationUtils
        nomeAzienda = ValidationUtils.sanitizeString(nomeAzienda);
        tipoAziendaStr = ValidationUtils.sanitizeString(tipoAziendaStr);
        nome = ValidationUtils.sanitizeString(nome);
        cognome = ValidationUtils.sanitizeString(cognome);
        email = ValidationUtils.normalizeEmail(email);
        
        // Validazione nome azienda
        if (!ValidationUtils.isValidCompanyName(nomeAzienda)) {
            req.setAttribute("errorMessage", "Il nome dell'azienda deve contenere almeno 2 caratteri.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
            return;
        }
        
        // Validazione tipo azienda
        if (!ValidationUtils.isValidCompanyType(tipoAziendaStr)) {
            req.setAttribute("errorMessage", "Seleziona un tipo di azienda valido.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
            return;
        }
        
        // Validazione campi utente
        if (!ValidationUtils.isValidName(nome)) {
            req.setAttribute("errorMessage", "Il nome deve contenere solo lettere e avere almeno 2 caratteri.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
            return;
        }
        
        if (!ValidationUtils.isValidName(cognome)) {
            req.setAttribute("errorMessage", "Il cognome deve contenere solo lettere e avere almeno 2 caratteri.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
            return;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            req.setAttribute("errorMessage", "Inserisci un indirizzo email valido.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
            return;
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            req.setAttribute("errorMessage", "La password deve contenere tra 6 e 100 caratteri.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
            return;
        }

        // Non più necessario creare istanza di UtenteDAO
        
        // Verifica se l'email esiste già
        try {
            if (UtenteDAO.findByEmail(email) != null) {
                req.setAttribute("errorMessage", "Un utente con questa email esiste già.");
                req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
                return;
            }
        } catch (SQLException e) {
            System.err.println("[REGISTER_AZIENDA ERROR] Errore durante la verifica email: " + e.getMessage());
            req.setAttribute("errorMessage", "Si è verificato un errore del sistema. Riprova più tardi.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
            return;
        }

        Long idAzienda = null;
        try {
            // 1. Crea l'azienda
            Azienda azienda = new Azienda();
            azienda.setNome(nomeAzienda);
            azienda.setTipo(tipoAziendaStr);
            idAzienda = AziendaDAO.create(azienda);

            if (idAzienda == null || idAzienda <= 0) {
                req.setAttribute("errorMessage", "Errore durante la creazione dell'azienda. Riprova.");
                req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
                return;
            }

            // 2. Crea l'utente e lo collega all'azienda
            Utente utente = new Utente();
            utente.setNome(nome);
            utente.setCognome(cognome);
            utente.setEmail(email);
            utente.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
            utente.setDataRegistrazione(Timestamp.from(Instant.now()));
            utente.setRuolo("azienda");

            boolean utenteCreato = UtenteDAO.create(utente);

            if (utenteCreato) {
                // TODO: Qui dovremmo associare l'utente all'azienda in una tabella di relazione
                // Per ora la registrazione è completata
                req.setAttribute("successMessage", "Registrazione azienda completata con successo! Ora puoi accedere.");
                req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
            } else {
                // Errore nella creazione utente - rollback dell'azienda
                try {
                    // TODO: Implementare cancellazione azienda per rollback
                    System.err.println("[REGISTER_AZIENDA ERROR] Fallback: dovremmo cancellare azienda ID: " + idAzienda);
                } catch (Exception rollbackError) {
                    System.err.println("[REGISTER_AZIENDA ERROR] Errore durante il rollback: " + rollbackError.getMessage());
                }
                req.setAttribute("errorMessage", "Errore durante la creazione dell'account utente. Riprova.");
                req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            System.err.println("[REGISTER_AZIENDA ERROR] Errore database: " + e.getMessage());
            e.printStackTrace();
            // Tentativo di rollback se possibile
            if (idAzienda != null && idAzienda > 0) {
                try {
                    // TODO: Implementare cancellazione azienda per rollback
                    System.err.println("[REGISTER_AZIENDA ERROR] Dovremmo fare rollback azienda ID: " + idAzienda);
                } catch (Exception rollbackError) {
                    System.err.println("[REGISTER_AZIENDA ERROR] Errore durante il rollback: " + rollbackError.getMessage());
                }
            }
            req.setAttribute("errorMessage", "Errore del database durante la registrazione dell'azienda. Riprova.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            System.err.println("[REGISTER_AZIENDA ERROR] Argomento non valido: " + e.getMessage());
            req.setAttribute("errorMessage", "Tipo di azienda non valido.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
        }
    }
}
