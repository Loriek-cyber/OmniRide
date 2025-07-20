package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.TrattaDAO;
import model.dao.udata.SessioneDAO;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Inizializza il carrello se non esiste
        Map<String, Object> carrello = (Map<String, Object>) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new HashMap<>();
            session.setAttribute("carrello", carrello);
        }
        
        // Gestisci azioni specifiche se presenti
        String action = request.getParameter("action");
        if (action != null) {
            handleAction(request, response, action, carrello);
            return;
        }
        
        // Inoltra alla pagina del carrello
        request.getRequestDispatcher("/carrello.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    private void handleAction(HttpServletRequest request, HttpServletResponse response, 
                            String action, Map<String, Object> carrello) 
            throws ServletException, IOException {
        
        switch (action) {
            case "addPercorso":
                // Aggiunge un percorso al carrello
                String percorsoIndex = request.getParameter("percorso");
                if (percorsoIndex != null) {
                    carrello.put("percorso_" + Instant.now().toEpochMilli(), percorsoIndex);
                    request.setAttribute("successMessage", "Percorso aggiunto al carrello!");
                }
                break;
                
            case "remove":
                // Rimuove un elemento dal carrello
                String itemId = request.getParameter("itemId");
                if (itemId != null) {
                    carrello.remove(itemId);
                    request.setAttribute("successMessage", "Elemento rimosso dal carrello!");
                }
                break;
                
            case "clear":
                // Svuota il carrello
                carrello.clear();
                request.setAttribute("successMessage", "Carrello svuotato!");
                break;
                
            default:
                // Azione non riconosciuta
                request.setAttribute("errorMessage", "Azione non valida!");
                break;
        }
        
        // Reindirizza alla pagina del carrello
        request.getRequestDispatcher("/carrello.jsp").forward(request, response);
    }
}
