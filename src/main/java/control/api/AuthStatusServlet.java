package control.api;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.udata.Utente;

import java.io.IOException;

@WebServlet("/api/auth/status")
public class AuthStatusServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        
        AuthStatusResponse authResponse = new AuthStatusResponse();
        
        if (session != null && session.getAttribute("utente") != null) {
            Utente utente = (Utente) session.getAttribute("utente");
            authResponse.isLoggedIn = true;
            authResponse.userId = utente.getId();
            authResponse.username = utente.getNome() + " " + utente.getCognome();
            authResponse.email = utente.getEmail();
        } else {
            authResponse.isLoggedIn = false;
        }
        
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(authResponse);
        
        response.getWriter().write(jsonResponse);
    }
    
    // Classe per la risposta JSON
    private static class AuthStatusResponse {
        boolean isLoggedIn;
        Long userId;
        String username;
        String email;
    }
}
