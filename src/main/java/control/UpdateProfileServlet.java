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

    private UtenteDAO utenteDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        utenteDAO = new UtenteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verifica se l'utente è loggato
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Reindirizza alla pagina di modifica profilo
        request.getRequestDispatcher("prvUser/editProfile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utenteCorrente = (Utente) session.getAttribute("utente");

        // Recupera i parametri dal form
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validazione input
        if (!validateInput(nome, cognome, email, password, confirmPassword, request)) {
            request.getRequestDispatcher("/prvUser/editProfile.jsp").forward(request, response);
            return;
        }

        try {
            // Verifica se l'email è già in uso da un altro utente
            if (!email.equals(utenteCorrente.getEmail()) ) {
                request.setAttribute("errorMessage", "L'email inserita è già utilizzata da un altro utente.");
                request.getRequestDispatcher("/prvUser/editProfile.jsp").forward(request, response);
                return;
            }

            // Crea un oggetto utente con i nuovi dati
            Utente utenteAggiornato = new Utente();
            utenteAggiornato.setId(utenteCorrente.getId());
            utenteAggiornato.setNome(nome.trim());
            utenteAggiornato.setCognome(cognome.trim());
            utenteAggiornato.setEmail(email.trim().toLowerCase());
            utenteAggiornato.setRuolo(utenteCorrente.getRuolo());
            utenteAggiornato.setDataRegistrazione(utenteCorrente.getDataRegistrazione());
            utenteAggiornato.setAvatar(utenteCorrente.getAvatar());

            // Se è stata inserita una nuova password, la cifra
            if (password != null && !password.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                utenteAggiornato.setPasswordHash(hashedPassword);
            } else {
                // Mantiene la password esistente
                utenteAggiornato.setPasswordHash(utenteCorrente.getPasswordHash());
            }

            // Aggiorna l'utente nel database
            boolean success = utenteDAO.update(utenteAggiornato);

            if (success) {
                // Aggiorna l'utente nella sessione
                session.setAttribute("utente", utenteAggiornato);
                request.setAttribute("successMessage", "Profilo aggiornato con successo!");
            } else {
                request.setAttribute("errorMessage", "Errore durante l'aggiornamento del profilo. Riprova.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore del database durante l'aggiornamento del profilo.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Si è verificato un errore imprevisto. Riprova.");
        }

        // Reindirizza alla pagina di modifica profilo con il messaggio
        request.getRequestDispatcher("/WEB-INF/views/user/editProfile.jsp").forward(request, response);
    }

    private boolean validateInput(String nome, String cognome, String email,
                                  String password, String confirmPassword,
                                  HttpServletRequest request) {

        // Validazione campi obbligatori
        if (nome == null || nome.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Il nome è obbligatorio.");
            return false;
        }

        if (cognome == null || cognome.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Il cognome è obbligatorio.");
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "L'email è obbligatoria.");
            return false;
        }

        // Validazione formato email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            request.setAttribute("errorMessage", "Formato email non valido.");
            return false;
        }

        // Validazione lunghezza campi
        if (nome.trim().length() > 100) {
            request.setAttribute("errorMessage", "Il nome non può superare i 100 caratteri.");
            return false;
        }

        if (cognome.trim().length() > 100) {
            request.setAttribute("errorMessage", "Il cognome non può superare i 100 caratteri.");
            return false;
        }

        if (email.trim().length() > 255) {
            request.setAttribute("errorMessage", "L'email non può superare i 255 caratteri.");
            return false;
        }

        // Validazione password (solo se inserita)
        if (password != null && !password.isEmpty()) {
            if (password.length() < 8) {
                request.setAttribute("errorMessage", "La password deve contenere almeno 8 caratteri.");
                return false;
            }

            if (password.length() > 255) {
                request.setAttribute("errorMessage", "La password non può superare i 255 caratteri.");
                return false;
            }

            // Verifica che le password corrispondano
            if (confirmPassword == null || !password.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "Le password non corrispondono.");
                return false;
            }
        }
        return true;
    }
}