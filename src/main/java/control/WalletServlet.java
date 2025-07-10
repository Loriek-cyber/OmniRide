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

        // Se l'utente non è loggato, lo reindirizzo al login con il parametro di redirect
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirectURL=/wallet");
            return;
        }

        // Se l'utente è loggato, mostro la pagina del portafoglio
        req.getRequestDispatcher("/prvUser/wallet.jsp").forward(req, resp);
    }
}
