package control.azienda;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.*;
import model.sdata.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "addAvvisi", value = "/prvAzienda/addAvvisi")

public class AddAvvisiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Tratta> tratte = TrattaDAO.getAllTratte();
            req.setAttribute("tratte", tratte);
            req.getRequestDispatcher("/prvAzienda/addAvvisi.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String descrizione = req.getParameter("descrizione");
        String[] idTratteStr = req.getParameterValues("id_tratte");
        if (descrizione != null && !descrizione.isEmpty() && idTratteStr != null && idTratteStr.length > 0) {
            try {
                Avvisi avviso = new Avvisi();
                avviso.setDescrizione(descrizione);
                List<Long> tratte = new ArrayList<>();
                for (String idTrattaStr : idTratteStr) {
                    tratte.add(Long.parseLong(idTrattaStr));
                }
                avviso.setId_tratte_coinvolte(tratte);
                AvvisiDAO.create(avviso);
                resp.sendRedirect(req.getContextPath() + "/visualizzaAvvisi");
            } catch (SQLException e) {
                throw new ServletException("Errore durante l'inserimento dell'avviso", e);
            }
        } else {
            req.setAttribute("errorMessage", "Per favore, compila tutti i campi.");
            doGet(req, resp);
        }
    }

}
