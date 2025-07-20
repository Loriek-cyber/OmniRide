package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.udata.Biglietto;
import control.CarrelloServlet.BigliettoCarrello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/selectTicketType")
public class SelectTicketTypeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verify required parameters for ticket selection
        String percorsoJson = request.getParameter("percorso");
        String data = request.getParameter("data");
        String orario = request.getParameter("orario");
        String prezzoBaseStr = request.getParameter("prezzo");
        
        // Debug log
        System.out.println("[SELECT_TICKET_TYPE] Parametri ricevuti:");
        System.out.println("  - percorso: " + percorsoJson);
        System.out.println("  - data: " + data);
        System.out.println("  - orario: " + orario);
        System.out.println("  - prezzo: " + prezzoBaseStr);

        if (percorsoJson == null || data == null || orario == null || prezzoBaseStr == null) {
            System.out.println("[SELECT_TICKET_TYPE] Parametri mancanti, redirect a visualizzaTratte");
            response.sendRedirect(request.getContextPath() + "/visualizzaTratte");
            return;
        }

        try {
            double prezzoBase = Double.parseDouble(prezzoBaseStr);

            // Calculate prices as double values for JSP formatting
            double prezzoNormale = Biglietto.calcolaPrezzo(prezzoBase, Biglietto.TipoBiglietto.NORMALE);
            double prezzoGiornaliero = Biglietto.calcolaPrezzo(prezzoBase, Biglietto.TipoBiglietto.GIORNALIERO);
            double prezzoAnnuale = Biglietto.calcolaPrezzo(prezzoBase, Biglietto.TipoBiglietto.ANNUALE);

            // Set attributes for the JSP
            request.setAttribute("percorso", percorsoJson);
            request.setAttribute("data", data);
            request.setAttribute("orario", orario);
            request.setAttribute("prezzoBase", prezzoBase);
            request.setAttribute("prezzoNormale", prezzoNormale);
            request.setAttribute("prezzoGiornaliero", prezzoGiornaliero);
            request.setAttribute("prezzoAnnuale", prezzoAnnuale);

            // Forward to ticket selection page
            request.getRequestDispatcher("/selectTicketType.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/visualizzaTratte");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Get form parameters
        String percorsoJson = request.getParameter("percorso");
        String data = request.getParameter("data");
        String orario = request.getParameter("orario");
        String tipoStr = request.getParameter("tipo");
        String quantitaStr = request.getParameter("quantita");
        String prezzoStr = request.getParameter("prezzo");
        
        if (percorsoJson == null || data == null || orario == null || 
            tipoStr == null || quantitaStr == null || prezzoStr == null) {
            response.sendRedirect(request.getContextPath() + "/visualizzaTratte");
            return;
        }
        
        try {
            Biglietto.TipoBiglietto tipo = Biglietto.TipoBiglietto.valueOf(tipoStr);
            int quantita = Integer.parseInt(quantitaStr);
            double prezzo = Double.parseDouble(prezzoStr);
            
            // Get or create cart
            @SuppressWarnings("unchecked")
            List<BigliettoCarrello> carrello = (List<BigliettoCarrello>) session.getAttribute("carrello");
            if (carrello == null) {
                carrello = new ArrayList<>();
                session.setAttribute("carrello", carrello);
            }
            
            // Logica migliorata per l'aggiunta al carrello
            BigliettoCarrello existing = null;
            
            // Cerca se esiste già un item simile nel carrello
            for (BigliettoCarrello item : carrello) {
                if (item.getData().equals(data) && 
                    item.getOrario().equals(orario) && 
                    Double.compare(item.getPrezzo(), prezzo) == 0 &&
                    item.getTipo() == tipo &&
                    item.getPercorsoJson().equals(percorsoJson)) {
                    existing = item;
                    break;
                }
            }

            if (existing != null) {
                // Aggiorna la quantità dell'item esistente
                existing.setQuantita(existing.getQuantita() + quantita);
                System.out.println("[SELECT_TICKET_TYPE] Aggiornata quantità item esistente: " + existing.getQuantita());
            } else {
                // Crea nuovo item per il carrello
                BigliettoCarrello nuovoBiglietto = new BigliettoCarrello();
                nuovoBiglietto.setPercorsoJson(percorsoJson);
                nuovoBiglietto.setData(data);
                nuovoBiglietto.setOrario(orario);
                nuovoBiglietto.setPrezzo(prezzo);
                nuovoBiglietto.setQuantita(quantita);
                nuovoBiglietto.setTipo(tipo);
                
                // Estrai un nome leggibile dal JSON del percorso
                String nome = extractRouteNameFromJson(percorsoJson);
                nuovoBiglietto.setNome(nome);
                
                carrello.add(nuovoBiglietto);
                System.out.println("[SELECT_TICKET_TYPE] Aggiunto nuovo item al carrello: " + nome);
            }

            // Salva il carrello aggiornato nella sessione
            session.setAttribute("carrello", carrello);
            
            // Log per debug
            System.out.println("[SELECT_TICKET_TYPE] Carrello aggiornato, totale items: " + carrello.size());
            
            // Redirect al carrello con un messaggio di successo
            session.setAttribute("successMessage", "Biglietto aggiunto al carrello con successo!");
            response.sendRedirect(request.getContextPath() + "/carrello");
            
        } catch (IllegalArgumentException e) {
            System.err.println("[SELECT_TICKET_TYPE] Errore parametri non validi: " + e.getMessage());
            session.setAttribute("errorMessage", "Parametri non validi per il tipo di biglietto.");
            response.sendRedirect(request.getContextPath() + "/visualizzaTratte");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[SELECT_TICKET_TYPE] Errore generico: " + e.getMessage());
            session.setAttribute("errorMessage", "Errore nell'aggiunta del biglietto al carrello.");
            response.sendRedirect(request.getContextPath() + "/visualizzaTratte");
        }
    }
    
    /**
     * Estrae un nome leggibile dal JSON del percorso
     * @param percorsoJson JSON string del percorso
     * @return Nome leggibile del percorso
     */
    private String extractRouteNameFromJson(String percorsoJson) {
        if (percorsoJson == null || percorsoJson.trim().isEmpty()) {
            return "Percorso Non Definito";
        }
        
        try {
            // Se il JSON contiene informazioni leggibili, prova ad estrarle
            if (percorsoJson.contains("fermataIn") && percorsoJson.contains("fermataOu")) {
                // Parsing semplificato per estrarre i nomi delle fermate
                String[] parts = percorsoJson.split("nome");
                if (parts.length >= 3) {
                    // Estrai il primo nome (fermata di partenza)
                    String partenza = extractNameFromJsonPart(parts[1]);
                    // Trova l'ultima occorrenza per la fermata di arrivo
                    String arrivo = extractNameFromJsonPart(parts[parts.length - 1]);
                    
                    if (!partenza.isEmpty() && !arrivo.isEmpty() && !partenza.equals(arrivo)) {
                        return partenza + " - " + arrivo;
                    }
                }
            }
            
            // Fallback: se contiene trattini, prova ad usare quelli
            if (percorsoJson.contains("-")) {
                String cleaned = percorsoJson.replace("[", "").replace("]", "").replace("\"", "");
                if (cleaned.length() > 0 && cleaned.length() < 100) {
                    return cleaned;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Errore nell'estrazione del nome dal percorso JSON: " + e.getMessage());
        }
        
        return "Percorso Personalizzato";
    }
    
    /**
     * Estrae un nome da una parte del JSON
     * @param jsonPart Parte del JSON contenente il nome
     * @return Nome estratto
     */
    private String extractNameFromJsonPart(String jsonPart) {
        if (jsonPart == null) return "";
        
        try {
            // Cerca il pattern: "nome":"valore"
            int colonIndex = jsonPart.indexOf(":");
            if (colonIndex != -1) {
                String afterColon = jsonPart.substring(colonIndex + 1).trim();
                if (afterColon.startsWith("\"")) {
                    int endQuote = afterColon.indexOf("\"", 1);
                    if (endQuote != -1) {
                        return afterColon.substring(1, endQuote);
                    }
                }
            }
        } catch (Exception e) {
            // Ignora errori di parsing
        }
        
        return "";
    }
}
