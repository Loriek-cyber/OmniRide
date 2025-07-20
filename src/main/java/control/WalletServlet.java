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

        // Se l'utente non è loggato, controlla se ha biglietti ospite nella sessione client-side

        if (session == null || session.getAttribute("utente") == null) {
            // Reindirizza alla pagina wallet ospite che gestirà i biglietti da sessionStorage
            req.getRequestDispatcher("/wallet.jsp").forward(req, resp);
            return;
        }

        if(session.getAttribute("utente") == null) {

        }

        // Se l'utente è loggato, mostro la pagina del portafoglio normale
        req.getRequestDispatcher("/prvUser/wallet.jsp").forward(req, resp);
    }
}
