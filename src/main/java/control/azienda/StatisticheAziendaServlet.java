package control.azienda;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.AziendaDAO;
import model.dao.BigliettiDAO;
import model.dao.TrattaDAO;
import model.udata.Biglietto;
import model.udata.Utente;
import model.udata.Azienda;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "statisticheAzienda", value = "/prvAzienda/statistiche")
public class StatisticheAziendaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || httpSession.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) httpSession.getAttribute("utente");

        if (!"azienda".equals(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/prvUser/dashboard");
            return;
        }

        try {
            Azienda azienda = AziendaDAO.fromIDutente(utente.getId());
            if (azienda != null) {
                request.setAttribute("azienda", azienda);
                int tratteAttive = TrattaDAO.getTratteByAzienda(azienda.getId()).size();
                double totaleRicavi = 0;
                List<Biglietto> biglietti = BigliettiDAO.getAll();
                for (Biglietto biglietto1 : biglietti) {
                    totaleRicavi+=biglietto1.getPrezzo();
                }
                int bigliettiVenduti = 42;

                request.setAttribute("tratteAttive", tratteAttive);
                request.setAttribute("totaleRicavi", String.format("%.2f", totaleRicavi));
                request.setAttribute("bigliettiVenduti", bigliettiVenduti);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore durante il caricamento delle statistiche.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/prvAzienda/statistiche.jsp");
        dispatcher.forward(request, response);
    }
}

