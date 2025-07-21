package control;

import model.dao.UtenteDAO;
import model.dao.udata.SessioneDAO;
import model.udata.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.Instant;

@WebServlet("/AvatarServlet")
public class AvatarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Verifica la sessione se presente
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            String sessionId = (String) httpSession.getAttribute("sessionId");
            if (sessionId != null) {
                try {
                    boolean sessioneValida = SessioneDAO.sessioneEsistente(sessionId);
                    if (sessioneValida) {
                        SessioneDAO.aggiornaUltimoAccesso(sessionId, Instant.now().getEpochSecond());
                    }
                } catch (SQLException e) {
                    System.out.println("[AVATAR ERROR] Errore durante la verifica della sessione: " + e.getMessage());
                }
            }
        }
        
        String userIdParam = request.getParameter("userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required.");
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid User ID format.");
            return;
        }

        // Non più necessario creare istanza di UtenteDAO
        try {
            byte[] avatarData = UtenteDAO.getAvatarByUserId(userId);

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