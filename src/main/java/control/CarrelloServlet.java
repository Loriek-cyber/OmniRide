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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Inizializza il carrello se non esiste
        List<Tratta> carrello = (List<Tratta>) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new ArrayList<>();
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
                            String action, List<Tratta> carrello) 
            throws ServletException, IOException {
        
        switch (action) {
            case "addTratta":
                // Aggiunge una tratta al carrello
                String trattaId = request.getParameter("trattaId");
                if (trattaId != null) {
                    try {
                        Long id = Long.parseLong(trattaId);
                        Tratta tratta = TrattaDAO.getById(id);
                        if (tratta != null && !carrello.contains(tratta)) {
                            carrello.add(tratta);
                            request.setAttribute("successMessage", "Tratta aggiunta al carrello!");
                        } else if (carrello.contains(tratta)) {
                            request.setAttribute("errorMessage", "La tratta è già presente nel carrello!");
                        } else {
                            request.setAttribute("errorMessage", "Tratta non trovata!");
                        }
                    } catch (NumberFormatException | SQLException e) {
                        request.setAttribute("errorMessage", "Errore nell'aggiunta della tratta al carrello!");
                        e.printStackTrace();
                    }
                }
                break;
                
            case "addPercorso":
                // Compatibilità con codice esistente
                String percorsoIndex = request.getParameter("percorso");
                if (percorsoIndex != null) {
                    try {
                        Long id = Long.parseLong(percorsoIndex);
                        Tratta tratta = TrattaDAO.getById(id);
                        if (tratta != null && !carrello.contains(tratta)) {
                            carrello.add(tratta);
                            request.setAttribute("successMessage", "Percorso aggiunto al carrello!");
                        }
                    } catch (NumberFormatException | SQLException e) {
                        request.setAttribute("errorMessage", "Errore nell'aggiunta del percorso!");
                        e.printStackTrace();
                    }
                }
                break;
                
            case "remove":
                // Rimuove una tratta dal carrello per indice
                String indexStr = request.getParameter("index");
                if (indexStr != null) {
                    try {
                        int index = Integer.parseInt(indexStr);
                        if (index >= 0 && index < carrello.size()) {
                            carrello.remove(index);
                            request.setAttribute("successMessage", "Tratta rimossa dal carrello!");
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "Indice non valido!");
                    }
                }
                break;
                
            case "removeById":
                // Rimuove una tratta dal carrello per ID
                String idToRemove = request.getParameter("trattaId");
                if (idToRemove != null) {
                    try {
                        Long id = Long.parseLong(idToRemove);
                        carrello.removeIf(tratta -> tratta.getId().equals(id));
                        request.setAttribute("successMessage", "Tratta rimossa dal carrello!");
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "ID tratta non valido!");
                    }
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
