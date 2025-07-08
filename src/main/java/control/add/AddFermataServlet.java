package control.add;

import error.ErrorPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dao.FermataDAO;
import model.sdata.Coordinate;
import model.sdata.Fermata;
import model.util.Geolock;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "addFermata", value = "/prvAzienda/addFermata")
public class AddFermataServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Mostra il form per aggiungere una fermata
        req.getRequestDispatcher("/prvAdmin/addFermata.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("nome");
        if(name == null || name.equals("")) {
            doGet(req, resp);
        }
        String indirizzo = req.getParameter("indirizzo");
        if(indirizzo == null || indirizzo.equals("")) {
            doGet(req, resp);
        }

        String tipo = req.getParameter("tipo");
        if(tipo == null || tipo.equals("")) {
            doGet(req, resp);
        }

        Fermata fermata = new Fermata();
        fermata.setNome(name);
        fermata.setIndirizzo(indirizzo);
        fermata.setTipo(Fermata.TipoFermata.valueOf(tipo.toUpperCase()));
        fermata.setAttiva(true);
        try {
            fermata.setCoordinate(Geolock.getCoordinatesFromAddress(fermata.getIndirizzo()));
        } catch (Exception e) {
            resp.sendError(534,"Errore nella geolock");
        }
        try {
            FermataDAO.insertFermata(fermata);
        } catch (SQLException e) {
            resp.sendError(500);
        }


    }
}
