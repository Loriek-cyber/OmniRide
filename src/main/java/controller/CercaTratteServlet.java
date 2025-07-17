package controller;

import model.sdata.Tratta;
import model.sdata.FermataTratta;
import model.dao.TrattaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/cercaTratte")
public class CercaTratteServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Recupera i parametri dal form
        String partenza = request.getParameter("partenza");
        String arrivo = request.getParameter("arrivo");
        String orarioStr = request.getParameter("orario");
        String dataStr = request.getParameter("data");
        String prezzoMaxStr = request.getParameter("prezzoMax");
        String durataMaxStr = request.getParameter("durataMax");
        String ordinamento = request.getParameter("ordinamento");
        
        // Validazione parametri obbligatori
        if (partenza == null || partenza.trim().isEmpty() || 
            arrivo == null || arrivo.trim().isEmpty() || 
            orarioStr == null || orarioStr.trim().isEmpty() || 
            dataStr == null || dataStr.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Tutti i campi di ricerca sono obbligatori.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }
        
        try {
            // Parse dei parametri temporali
            LocalTime orario = LocalTime.parse(orarioStr);
            LocalDate data = LocalDate.parse(dataStr);
            
            // Recupera tutte le tratte dal DAO
            List<Tratta> tratte = TrattaDAO.findByPartenzaArrivoDataOrario(
                partenza.trim(), arrivo.trim(), data, orario
            );
            
            // Applica filtri opzionali
            if (prezzoMaxStr != null && !prezzoMaxStr.isEmpty()) {
                double prezzoMax = Double.parseDouble(prezzoMaxStr);
                tratte = tratte.stream()
                    .filter(t -> t.getCosto() <= prezzoMax)
                    .collect(Collectors.toList());
            }
            
            if (durataMaxStr != null && !durataMaxStr.isEmpty()) {
                double durataMax = Double.parseDouble(durataMaxStr);
                tratte = tratte.stream()
                    .filter(t -> {
                        // Calcola la durata in ore
double durata = calcolaDurataInOre(t);
                        return durata <= durataMax;
                    })
                    .collect(Collectors.toList());
            }
            
            // Applica ordinamento
            if (ordinamento != null) {
                switch (ordinamento) {
                    case "prezzo":
                        tratte.sort((a, b) -> Double.compare(a.getCosto(), b.getCosto()));
                        break;
                    case "durata":
                        tratte.sort((a, b) -> {
                            double durataA = calcolaDurataInOre(a);
                            double durataB = calcolaDurataInOre(b);
                            return Double.compare(durataA, durataB);
                        });
                        break;
                    case "partenza":
                        tratte.sort((a, b) -> {
                            LocalTime timeA = a.getOrarioPartenza();
                            LocalTime timeB = b.getOrarioPartenza();
                            if (timeA == null && timeB == null) return 0;
                            if (timeA == null) return 1;
                            if (timeB == null) return -1;
                            return timeA.compareTo(timeB);
                        });
                        break;
                }
            }
            
            // Passa i risultati alla JSP
            request.setAttribute("tratte", tratte);
            
            // Forward alla pagina dei risultati
            request.getRequestDispatcher("/risultati-ricerca.jsp").forward(request, response);
            
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Errore nel formato di data o ora
            request.setAttribute("errorMessage", "Formato data o ora non valido. Usa il formato corretto.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Errore nel formato numerico
            request.setAttribute("errorMessage", "Formato numerico non valido per prezzo o durata.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            // Log dettagliato dell'errore
            System.err.println("[CercaTratteServlet] Errore durante la ricerca:");
            System.err.println("Partenza: " + partenza);
            System.err.println("Arrivo: " + arrivo);
            System.err.println("Data: " + dataStr);
            System.err.println("Orario: " + orarioStr);
            System.err.println("Errore: " + e.getMessage());
            
            // In caso di errore generico, mostra un messaggio più dettagliato
            request.setAttribute("errorMessage", "Si è verificato un errore durante la ricerca. Riprova più tardi.");
            request.setAttribute("errorDetails", e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    private double calcolaDurataInOre(Tratta tratta) {
        if (tratta == null || tratta.getFermataTrattaList() == null || tratta.getFermataTrattaList().isEmpty()) {
            return 0.0;
        }
        // Somma tutti i tempi di percorrenza tra le fermate (esclusa l'ultima che non ha prossima fermata)
        int tempoTotaleMinuti = 0;
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        for (int i = 0; i < fermate.size() - 1; i++) {
            FermataTratta fermata = fermate.get(i);
            if (fermata != null) {
                tempoTotaleMinuti += fermata.getTempoProssimaFermata();
            }
        }
        return tempoTotaleMinuti / 60.0;
    }
}
