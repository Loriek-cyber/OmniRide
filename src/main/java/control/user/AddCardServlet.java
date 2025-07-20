package control.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.Carta_CreditoDAO;
import model.udata.CartaCredito;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "addCard", value = "/addCard")
public class AddCardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utente ut = null;
        if(session.getAttribute("utente") == null || session==null) {
            resp.sendRedirect("/login");
        }
        else {
            ut = (Utente) session.getAttribute("utente");
        }

        String intestatario = req.getParameter("intestatario");
        String numero_carta = req.getParameter("numero_carta");
        String data = req.getParameter("data");
        String cvv = req.getParameter("cvv");

        CartaCredito cartaCredito = new CartaCredito();
        cartaCredito.setNumeroCarta(numero_carta);
        cartaCredito.setCvv(cvv);
        cartaCredito.setData_scadenza(data);
        cartaCredito.setNome_intestatario(intestatario);
        cartaCredito.setId_utente(ut.getId());

        try {
            Carta_CreditoDAO.create(cartaCredito);
            resp.sendRedirect(req.getContextPath() + "/prvUser/card?success=add");
        } catch (SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/prvUser/card?error=database_error");
        }
    }
}
