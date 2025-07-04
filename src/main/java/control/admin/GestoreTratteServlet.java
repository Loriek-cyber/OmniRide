package control.admin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.TrattaDAO;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/gestoreTratte")
public class GestoreTratteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("ruolo").equals("azienda") || session.getAttribute("ruolo").equals("admin"))) {
            response.sendRedirect(request.getContextPath() + "/noAccess.jsp");
            return;
        }
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
