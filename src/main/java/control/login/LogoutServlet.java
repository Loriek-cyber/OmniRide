package control.login;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.udata.SessioneDAO;
import model.udata.Sessione;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession httpSession = req.getSession(false); // Prendo la sessione esistente senza crearne una nuova

        if (httpSession != null) {
            // Invalido la sessione personalizzata nel database
            String sessionId = (String) httpSession.getAttribute("sessionId");
            if (sessionId != null) {
                try {
                    SessioneDAO.invalidaSessione(sessionId);
                    System.out.println("[LOGOUT DEBUG] Sessione invalidata nel database: " + sessionId);
                } catch (SQLException e) {
                    System.out.println("[LOGOUT ERROR] Errore durante l'invalidazione della sessione: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            httpSession.invalidate(); // Invalido la sessione HTTP
        }

        // Reindirizzo alla pagina di login con un messaggio di successo
        req.setAttribute("successMessage", "Logout effettuato con successo.");
        req.getRequestDispatcher("/login/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
