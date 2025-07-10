package control.add;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dao.*;
import model.sdata.*;
import model.udata.Azienda;
import model.udata.Utente;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "addTratta", value = "/prvAzienda/addTratta")
public class AddTrattaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("fermate",FermataDAO.getAll());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        req.getRequestDispatcher("/prvAzienda/addTratta.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false); // false: non crea nuova sessione se non esiste

        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;
        if (utente == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utente non autenticato.");
            return;
        }

        //Ho rimosso l'autorizzazione per le aziende per problemi

        String nome = req.getParameter("nome");
        if(nome == null) {
            req.setAttribute("errore", "Nome non inserito.");
            doGet(req, resp);
        }
        double costo = Double.parseDouble(req.getParameter("costo"));

        if(costo<0){
            req.setAttribute("errore", "Costo non inserito o minore di zero.");
            doGet(req, resp);
        }

        try {
            List<Fermata> fermataList = FermataDAO.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        String fermateStr = req.getParameter("fermateSelezionate");
        String tempiStr = req.getParameter("tempiTraFermate");


        //impostiamo i dati iniziali che ovviamente sono stati gia impostati
        Tratta tratta = new Tratta();
        tratta.setNome(nome);
        tratta.setAttiva(true);
        tratta.setCosto(costo);

        List<FermataTratta> fermataTrattaList = new ArrayList<>();



    }
}
