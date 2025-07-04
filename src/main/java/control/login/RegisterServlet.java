package control.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.UtenteDAO;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;

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
        String password = req.getParameter("password");

        UtenteDAO utenteDAO = new UtenteDAO();

        // 1. Controlla se l'email esiste già
        try {
            if (UtenteDAO.findByEmail(email) != null) {
                req.setAttribute("errorMessage", "Un account con questa email esiste già. Prova ad accedere.");
                req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 2. Crea il nuovo utente
        Utente nuovoUtente = new Utente();
        nuovoUtente.setNome(nome);
        nuovoUtente.setCognome(cognome);
        nuovoUtente.setEmail(email);
        nuovoUtente.setPasswordHash(password); // Il DAO si occuperà dell'hashing

        boolean isCreated = utenteDAO.create(nuovoUtente);

        if (isCreated) {
            // 3. Successo: invia alla pagina di login con un messaggio
            req.setAttribute("successMessage", "Registrazione completata con successo! Ora puoi accedere.");
            req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
        } else {
            // 4. Errore: invia di nuovo alla pagina di registrazione con un errore generico
            req.setAttribute("errorMessage", "Si è verificato un errore durante la registrazione. Riprova.");
            req.getRequestDispatcher("/register/register.jsp").forward(req, resp);
        }
    }
}
