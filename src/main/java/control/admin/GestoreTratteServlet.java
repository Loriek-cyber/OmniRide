package control.admin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.TrattaDAO;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/gestoreTratte")
public class GestoreTratteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Tratta> tratte = TrattaDAO.getAllTratte();
            request.setAttribute("tratte", tratte);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/prvAdmin/gestoreTratte.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Errore nel recupero delle tratte", e);
        }
    }
}
