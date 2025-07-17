package control.search;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.FermataDAO;
import model.dao.TrattaDAO;
import model.pathfinding.Pathfinder;
import model.pathfinding.Percorso;
import model.sdata.Fermata;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(SearchServlet.class.getName());
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Recupera i parametri dalla richiesta
            String partenzaStr = request.getParameter("partenza");
            String arrivoStr = request.getParameter("arrivo");
            String dataStr = request.getParameter("data");
            String orarioStr = request.getParameter("orario");
            
            // Parametri opzionali per i filtri
            String prezzoMaxStr = request.getParameter("prezzoMax");
            String durataMaxStr = request.getParameter("durataMax");
            String tipoOrdinamento = request.getParameter("ordinamento"); // "prezzo", "durata", "partenza"
            
            // Validazione parametri obbligatori
            if (partenzaStr == null || arrivoStr == null || dataStr == null || orarioStr == null) {
                sendErrorResponse(response, "Parametri obbligatori mancanti");
                return;
            }
            
            // Cerca le fermate per nome
            List<Fermata> fermatePartenza = FermataDAO.ricercaPerNome(partenzaStr);
            List<Fermata> fermateArrivo = FermataDAO.ricercaPerNome(arrivoStr);
            
            if (fermatePartenza.isEmpty()) {
                sendErrorResponse(response, "Fermata di partenza non trovata: " + partenzaStr);
                return;
            }
            
            if (fermateArrivo.isEmpty()) {
                sendErrorResponse(response, "Fermata di arrivo non trovata: " + arrivoStr);
                return;
            }
            
            // Prende la prima fermata trovata (si può migliorare con logica di disambiguazione)
            Fermata fermataPartenza = fermatePartenza.get(0);
            Fermata fermataArrivo = fermateArrivo.get(0);
            
            // Recupera tutte le tratte attive
            List<Tratta> tutteLeTratte = TrattaDAO.doRetrieveAll();
            List<Fermata> tutteLeFermate = FermataDAO.doRetrieveAll();
            
            // Filtra solo le tratte attive
            List<Tratta> tratteAttive = tutteLeTratte.stream()
                    .filter(Tratta::isAttiva)
                    .filter(tratta -> tratta.hasOrariAttivi())
                    .toList();
            
            // Trova i percorsi usando il pathfinder
            List<Percorso> percorsiTrovati = new ArrayList<>();

            Percorso percorsoMigliore = Pathfinder.trovaPercorsoMigliore(
                fermataPartenza, fermataArrivo, tratteAttive, tutteLeFermate);

            if (percorsoMigliore != null && !percorsoMigliore.getSegmenti().isEmpty()) {
                percorsiTrovati.add(percorsoMigliore);
            }

            // Applica i filtri se specificati
            if (prezzoMaxStr != null) {
                try {
                    double prezzoMax = Double.parseDouble(prezzoMaxStr);
                    percorsiTrovati = percorsiTrovati.stream()
                            .filter(p -> p.getCosto() <= prezzoMax)
                            .toList();
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Formato prezzo non valido: " + prezzoMaxStr);
                }
            }
            
            if (durataMaxStr != null) {
                try {
                    double durataMax = Double.parseDouble(durataMaxStr);
                    percorsiTrovati = percorsiTrovati.stream()
                            .filter(p -> p.getCosto() <= durataMax) // Usando getCosto() come proxy per la durata
                            .toList();
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Formato durata non valido: " + durataMaxStr);
                }
            }
            
            // Ordina i risultati
            if (tipoOrdinamento != null) {
                switch (tipoOrdinamento.toLowerCase()) {
                    case "prezzo":
                        percorsiTrovati.sort((p1, p2) -> Double.compare(p1.getCosto(), p2.getCosto()));
                        break;
                    case "durata":
                        percorsiTrovati.sort((p1, p2) -> Double.compare(p1.getCosto(), p2.getCosto()));
                        break;
                    // Altri tipi di ordinamento...
                }
            }
            
            // Prepara la risposta JSON
            SearchResponse searchResponse = new SearchResponse();
            searchResponse.success = true;
            searchResponse.percorsi = percorsiTrovati;
            searchResponse.fermataPartenza = fermataPartenza;
            searchResponse.fermataArrivo = fermataArrivo;
            searchResponse.data = dataStr;
            searchResponse.orario = orarioStr;
            
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(searchResponse);
            
            response.getWriter().write(jsonResponse);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore database durante la ricerca", e);
            sendErrorResponse(response, "Errore del database");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore generico durante la ricerca", e);
            sendErrorResponse(response, "Errore interno del server");
        }
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        SearchResponse errorResponse = new SearchResponse();
        errorResponse.success = false;
        errorResponse.error = message;
        
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(errorResponse);
        
        response.getWriter().write(jsonResponse);
    }
    
    // Classe per la risposta JSON
    private static class SearchResponse {
        boolean success;
        String error;
        List<Percorso> percorsi;
        Fermata fermataPartenza;
        Fermata fermataArrivo;
        String data;
        String orario;
    }
}
