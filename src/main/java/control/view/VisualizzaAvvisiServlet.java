package control.view;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.sdata.Avvisi;
import model.sdata.Tratta;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "visualizzaAvvisi", value = "/visualizzaAvvisi")
public class VisualizzaAvvisiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Recupero dati dal ServletContext
        List<Avvisi> avvisi = (List<Avvisi>) getServletContext().getAttribute("avvisi");
        Map<Long, Tratta> tratteMap = (Map<Long, Tratta>) getServletContext().getAttribute("tratteMap");

        // Controllo che i dati non siano null (opzionale ma consigliato)
        /* Seazione di Debug
        if (avvisi == null || tratteMap == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Dati non disponibili nel context.");
            return;
        }*/

        // Imposto gli attributi da passare alla JSP
        req.setAttribute("avvisi", avvisi);
        req.setAttribute("tratteMap", tratteMap);

        // Inoltro alla JSP
        req.getRequestDispatcher("/avvisi.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Per semplicità, il POST si comporta come il GET
        doGet(req, resp);
    }
}
