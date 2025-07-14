package control.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.UtenteDAO;
import model.udata.Utente;
import model.utils.ValidationUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nome = req.getParameter("nome");
        String cognome = req.getParameter("cognome");
        String email = req.getParameter("email");
        email = email.toLowerCase();
        String password = req.getParameter("password");

        // Normalizzazione e validazione input usando ValidationUtils
        nome = ValidationUtils.sanitizeString(nome);
        cognome = ValidationUtils.sanitizeString(cognome);
        email = ValidationUtils.normalizeEmail(email);
        
        // Validazione campi obbligatori
        if (!ValidationUtils.isValidName(nome)) {
            req.setAttribute("errorMessage", "Il nome deve contenere solo lettere e avere almeno 2 caratteri.");
            req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
            return;
        }
        
        if (!ValidationUtils.isValidName(cognome)) {
            req.setAttribute("errorMessage", "Il cognome deve contenere solo lettere e avere almeno 2 caratteri.");
            req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
            return;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            req.setAttribute("errorMessage", "Inserisci un indirizzo email valido.");
            req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
            return;
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            req.setAttribute("errorMessage", "La password deve contenere tra 6 e 100 caratteri.");
            req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
            return;
        }

        // Non più necessario creare istanza di UtenteDAO

        // 1. Controlla se l'email esiste già
        try {
            if (UtenteDAO.findByEmail(email) != null) {
                req.setAttribute("errorMessage", "Un account con questa email esiste già. Prova ad accedere.");
                req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
                return;
            }
        } catch (SQLException e) {
            System.err.println("[REGISTER ERROR] Errore durante la verifica email: " + e.getMessage());
            req.setAttribute("errorMessage", "Si è verificato un errore del sistema. Riprova più tardi.");
            req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
            return;
        }

        // 2. Crea il nuovo utente
        Utente nuovoUtente = new Utente();
        nuovoUtente.setNome(nome);
        nuovoUtente.setCognome(cognome);
        nuovoUtente.setEmail(email);
        nuovoUtente.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        nuovoUtente.setDataRegistrazione(Timestamp.from(Instant.now()));
        nuovoUtente.setRuolo("utente"); // Ruolo di default

        try {
            Long success = UtenteDAO.create(nuovoUtente);
            if (success!=null) {
                req.setAttribute("successMessage", "Registrazione completata con successo! Ora puoi accedere.");
                req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
            } else {
                req.setAttribute("errorMessage", "Si è verificato un errore durante la registrazione. Riprova.");
                req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            System.err.println("[REGISTER ERROR] Errore durante la creazione utente: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("errorMessage", "Si è verificato un errore durante la registrazione. Riprova.");
            req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
        }
    }
}
