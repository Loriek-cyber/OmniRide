package control;

import model.dao.UtenteDAO;
import model.udata.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

@WebServlet("/AvatarServlet")
public class AvatarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdParam = request.getParameter("userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required.");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid User ID format.");
            return;
        }

        UtenteDAO utenteDAO = new UtenteDAO();
        try {
            byte[] avatarData = utenteDAO.getAvatarByUserId(userId);

            if (avatarData != null && avatarData.length > 0) {
                response.setContentType("image/png"); // O "image/jpeg" a seconda del formato delle immagini salvate
                response.setContentLength(avatarData.length);
                OutputStream out = response.getOutputStream();
                out.write(avatarData);
                out.flush();
            } else {
                // Se l'avatar non è trovato, reindirizza a un'immagine di default
                response.sendRedirect(request.getContextPath() + "/Images/default_avatar.png");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error while retrieving avatar.", e);
        }
    }
}