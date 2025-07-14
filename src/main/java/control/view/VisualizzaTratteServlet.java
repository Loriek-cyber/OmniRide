package control.view;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.TrattaDAO;
import model.sdata.OrarioTratta;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "visualizzaTratte", value = "/visualizzaTratte")
public class VisualizzaTratteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Retrieve tratte data from the servlet context
        @SuppressWarnings("unchecked")
        List<Tratta> tratte = (List<Tratta>) getServletContext().getAttribute("tratte");



        // Use the data as needed
        if (tratte != null) {
            // Set the data as a request attribute to use in JSP
            req.setAttribute("tratte", tratte);
            req.getRequestDispatcher("/tratte.jsp").forward(req, resp);
        }
    }
}
