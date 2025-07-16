package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/wallet")
public class WalletServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Se l'utente non è loggato, controlla se ha biglietti ospite
        if (session == null || session.getAttribute("utente") == null) {
            // Controlla se ci sono biglietti ospite
            if (session != null && session.getAttribute("guestTickets") != null) {
                // Reindirizza al portafoglio ospite
                resp.sendRedirect(req.getContextPath() + "/guest-wallet");
                return;
            }
            
            // Nessun biglietto ospite, reindirizza al login
            resp.sendRedirect(req.getContextPath() + "/login?redirectURL=/wallet");
            return;
        }

        // Se l'utente è loggato, mostro la pagina del portafoglio
        req.getRequestDispatcher("/prvUser/wallet.jsp").forward(req, resp);
    }
}
