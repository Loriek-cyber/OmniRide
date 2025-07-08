package control.add;

import error.ErrorPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dao.FermataDAO;
import model.dao.FermataTrattaDAO;
import model.dao.TrattaDAO;
import model.sdata.Fermata;
import model.sdata.FermataTratta;
import model.sdata.Tratta;
import model.udata.Azienda;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "addTratta", value = "/prvAzienda/addTratta")
public class AddTrattaServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Recupera tutte le fermate disponibili
            List<Fermata> fermate = FermataDAO.doRetrieveAll();
            req.setAttribute("fermate", fermate);
            req.getRequestDispatcher("/prvAzienda/addTratta.jsp").forward(req, resp);
        } catch (SQLException e) {
            resp.sendError(500,"Errore nel collegamento al database");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nome = req.getParameter("nome");
        if(nome == null || nome.equals("")) {
            doGet(req, resp);
        }
        double costo = Double.parseDouble(req.getParameter("costo"));
        if(costo <= 0.0) {
            doGet(req, resp);
        }



    }
}
