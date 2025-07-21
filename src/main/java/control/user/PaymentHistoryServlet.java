package control.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.BigliettiDAO;
import model.udata.Biglietto;
import model.udata.Utente;

import java.io.IOException;
import java.util.List;

@WebServlet("/storicoPagamenti")
public class PaymentHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utente utente = (Utente) req.getSession().getAttribute("utente");
        if (utente == null) {
            resp.sendRedirect(req.getContextPath() + "/login/login.jsp");
            return;
        }
        List<Biglietto> biglietti = BigliettiDAO.getAllUser(utente.getId());
        req.setAttribute("biglietti", biglietti);
        req.getRequestDispatcher("/prvUser/storicoPagamenti.jsp").forward(req, resp);
    }
}
