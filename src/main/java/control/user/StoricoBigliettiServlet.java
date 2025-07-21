package control.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.BigliettiDAO;
import model.udata.Utente;
import model.udata.Biglietto;

import java.io.IOException;
import java.util.List;

@WebServlet("/storicoBiglietti")
public class StoricoBigliettiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utente utente = (Utente) req.getSession().getAttribute("utente");
        if (utente == null) {
            resp.sendRedirect(req.getContextPath() + "/login/login.jsp");
            return;
        }
        List<Biglietto> biglietti = BigliettiDAO.getAllUser(utente.getId());
        req.setAttribute("biglietti", biglietti);
        req.getRequestDispatcher("/prvUser/storicoBiglietti.jsp").forward(req, resp);
    }
} 