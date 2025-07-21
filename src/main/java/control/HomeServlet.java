package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.AvvisiDAO;
import model.dao.FermataDAO;
import model.sdata.Avvisi;
import model.sdata.Fermata;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "HomeServlet", value = "/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        @SuppressWarnings("unchecked")
        List<Fermata> fermate = (List<Fermata>) getServletContext().getAttribute("fermate");
        request.setAttribute("fermate", fermate);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
