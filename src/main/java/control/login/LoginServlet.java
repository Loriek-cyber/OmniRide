package control.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.UtenteDAO;
import model.udata.Utente;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

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

        UtenteDAO utenteDAO = new UtenteDAO();
        Utente utente = utenteDAO.findByEmail(email);

        // BCrypt.checkpw richiede che il sale sia generato e incluso nell'hash
        // La nostra tabella 'Utente' ha 'password_hash', che dovrebbe già contenere il sale.
        if (utente != null && BCrypt.checkpw(password, utente.getPasswordHash())) {
            // Login successo: creo la sessione e salvo l'utente
            HttpSession session = req.getSession(true);
            session.setAttribute("utente", utente);

            // Reindirizzo alla dashboard
            resp.sendRedirect(req.getContextPath() + "/prvUser/dashboard.jsp");
        } else {
            // Login fallito: imposto un messaggio di errore e rimando alla pagina di login
            req.setAttribute("errorMessage", "Email o password non validi. Riprova.");
            req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
        }
    }
}
