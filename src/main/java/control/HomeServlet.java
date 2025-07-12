package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.AvvisiDAO;
import model.dao.TrattaDAO;
import model.sdata.Avvisi;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "HomeServlet", value = "/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Recupera tutti gli avvisi dal database
            List<Avvisi> avvisi = AvvisiDAO.getAllAvvisi();
            
            // Crea una lista di avvisi arricchiti con informazioni delle tratte
            List<Map<String, Object>> avvisiArricchiti = new ArrayList<>();
            
            // Limita a massimo 5 avvisi per la homepage
            int maxAvvisi = Math.min(avvisi.size(), 5);
            
            for (int i = 0; i < maxAvvisi; i++) {
                Avvisi avviso = avvisi.get(i);
                Map<String, Object> avvisoArricchito = new HashMap<>();
                
                // Dati base dell'avviso
                avvisoArricchito.put("id", avviso.getId());
                avvisoArricchito.put("descrizione", avviso.getDescrizione());
                
                // Recupera informazioni sulla prima tratta coinvolta
                String nomeTratta = "Tratta non trovata";
                boolean trattaAttiva = false;
                
                if (avviso.getId_tratte_coinvolte() != null && !avviso.getId_tratte_coinvolte().isEmpty()) {
                    Long trattaId = avviso.getId_tratte_coinvolte().get(0);
                    try {
                        nomeTratta = TrattaDAO.getTrattaNameByID(trattaId);
                        if (nomeTratta == null) {
                            nomeTratta = "Tratta ID: " + trattaId;
                        }
                        
                        // Controlla se la tratta è attiva
                        var tratta = TrattaDAO.getById(trattaId);
                        if (tratta != null) {
                            trattaAttiva = tratta.isAttiva();
                        }
                    } catch (SQLException e) {
                        System.err.println("Errore nel recupero della tratta " + trattaId + ": " + e.getMessage());
                        nomeTratta = "Tratta ID: " + trattaId;
                    }
                }
                
                avvisoArricchito.put("nomeTratta", nomeTratta);
                avvisoArricchito.put("trattaAttiva", trattaAttiva);
                
                avvisiArricchiti.add(avvisoArricchito);
            }
            
            // Passa gli avvisi arricchiti alla JSP
            request.setAttribute("avvisiArricchiti", avvisiArricchiti);
            request.setAttribute("avvisi", avvisi); // Mantieni per compatibilità
            
        } catch (SQLException e) {
            // Logga l'errore e imposta un attributo di errore per la JSP
            System.err.println("Errore nel recupero degli avvisi: " + e.getMessage());
            request.setAttribute("avvisiError", "Errore nel caricamento degli avvisi: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
