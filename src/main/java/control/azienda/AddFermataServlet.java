package control.azienda;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dao.FermataDAO;
import model.sdata.Coordinate;
import model.sdata.Fermata;
import model.udata.Utente;
import model.util.Geolock;
import java.io.IOException;
import java.sql.SQLException;


@WebServlet(name = "addFermata", value = "/prvAzienda/addFermata")
public class AddFermataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/prvAzienda/addFermata.jsp").forward(req, resp);
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
        String indirizzo = req.getParameter("indirizzo");
        String tipoParam = req.getParameter("tipo");

        if (nome == null || indirizzo == null || tipoParam == null ||
                nome.isBlank() || indirizzo.isBlank() || tipoParam.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti o non validi.");
            return;
        }

        //da Aggiungere un altro controllo dei dati in modo da vedere se una fermata simile esiste

        Fermata.TipoFermata tipoFermata;
        try {
            tipoFermata = Fermata.TipoFermata.valueOf(tipoParam);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo fermata non valido.");
            return;
        }

        Coordinate coord;
        try {
            coord = Geolock.getCoordinatesFromAddress(indirizzo);
        } catch (Exception e) {
            // e.printStackTrace(); // Debug log
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore con le risorse esterne (GeoLock).");
            return;
        }

        Fermata nuovaFermata = new Fermata();
        nuovaFermata.setNome(nome);
        nuovaFermata.setIndirizzo(indirizzo);
        nuovaFermata.setCoordinate(coord);
        nuovaFermata.setTipo(tipoFermata);

        try {
            FermataDAO.create(nuovaFermata);
        } catch (SQLException e) {
            // e.printStackTrace(); // Debug log
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante la creazione della fermata.");
            return;
        }

        // Reindirizza alla dashboard azienda
        resp.sendRedirect(req.getContextPath() + "/prvAzienda/dashboard");
    }
}
