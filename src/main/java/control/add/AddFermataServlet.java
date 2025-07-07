package control.add;

import error.ErrorPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.sdata.Coordinate;
import model.sdata.Fermata;
import model.util.Geolock;

import java.io.IOException;
@WebServlet(name = "addFermata", value = "/prvAzienda/addFermata")
public class AddFermataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nome = req.getParameter("nome");
        String indirizzo = req.getParameter("indirizzo");
        Coordinate cordinate;
        try {
             cordinate= Geolock.getCoordinatesFromAddress(indirizzo);
        } catch (Exception e) {
            ErrorPage errorPage = new ErrorPage(542,"Errore con il geolock");
            req.setAttribute("error", errorPage);
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }
        Fermata.TipoFermata tipo = Fermata.TipoFermata.valueOf(req.getParameter("tipo"));
        Fermata fermata = new Fermata();
        fermata.setNome(nome);
        fermata.setIndirizzo(indirizzo);
        fermata.setCoordinate(cordinate);
        fermata.setTipo(tipo);
        fermata.setAttiva(true);
    }
}
