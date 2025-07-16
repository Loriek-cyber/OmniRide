package control.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.BigliettiDAO;
import model.dao.TrattaDAO;
import model.pathfinding.Percorso;
import model.sdata.Tratta;
import model.udata.Biglietto;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/payment/process")
public class PaymentProcessServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(PaymentProcessServlet.class.getName());
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Legge i dati JSON dalla richiesta
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                jsonBuilder.append(line);
            }
            
            Gson gson = new Gson();
            JsonObject requestData = gson.fromJson(jsonBuilder.toString(), JsonObject.class);
            
            // Estrae i dati dalla richiesta
            Percorso percorso = gson.fromJson(requestData.get("percorso"), Percorso.class);
            String data = requestData.get("data").getAsString();
            String orario = requestData.get("orario").getAsString();
            boolean isGuest = requestData.get("isGuest").getAsBoolean();
            JsonObject paymentData = requestData.getAsJsonObject("paymentData");
            
            // Valida i dati di pagamento
            if (!validatePaymentData(paymentData)) {
                sendErrorResponse(response, "Dati di pagamento non validi");
                return;
            }
            
            // Simula il processo di pagamento
            if (!processPayment(paymentData)) {
                sendErrorResponse(response, "Pagamento rifiutato dalla banca");
                return;
            }
            
            // Crea il biglietto
            Biglietto biglietto = createTicket(percorso, data, orario, isGuest, request);
            
            if (biglietto == null) {
                sendErrorResponse(response, "Errore nella creazione del biglietto");
                return;
            }
            
            // Salva il biglietto nel database
            Long ticketId = saveBiglietto(biglietto);
            
            if (ticketId == null) {
                sendErrorResponse(response, "Errore nel salvataggio del biglietto");
                return;
            }
            
            // Se è un ospite, salva il biglietto nella sessione
            if (isGuest) {
                saveGuestTicket(request, biglietto);
            }
            
            // Risposta di successo
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.success = true;
            paymentResponse.ticketId = ticketId.toString();
            paymentResponse.message = "Pagamento completato con successo";
            
            String jsonResponse = gson.toJson(paymentResponse);
            response.getWriter().write(jsonResponse);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante il processo di pagamento", e);
            sendErrorResponse(response, "Errore interno del server");
        }
    }
    
    /**
     * Valida i dati di pagamento
     */
    private boolean validatePaymentData(JsonObject paymentData) {
        if (paymentData == null) return false;
        
        String cardNumber = paymentData.get("cardNumber").getAsString();
        String expiryDate = paymentData.get("expiryDate").getAsString();
        String cvv = paymentData.get("cvv").getAsString();
        String cardHolder = paymentData.get("cardHolder").getAsString();
        
        // Validazione base
        return cardNumber != null && cardNumber.length() >= 13 &&
               expiryDate != null && expiryDate.matches("\\d{2}/\\d{2}") &&
               cvv != null && cvv.length() >= 3 &&
               cardHolder != null && !cardHolder.trim().isEmpty();
    }
    
    /**
     * Simula il processo di pagamento
     */
    private boolean processPayment(JsonObject paymentData) {
        // Simulazione: accetta tutti i pagamenti tranne alcune carte di test
        String cardNumber = paymentData.get("cardNumber").getAsString();
        
        // Simula carte rifiutate
        if (cardNumber.contains("0000") || cardNumber.contains("1111")) {
            return false;
        }
        
        // Simula un breve ritardo per il processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return true;
    }
    
    /**
     * Crea il biglietto
     */
    private Biglietto createTicket(Percorso percorso, String data, String orario, boolean isGuest, HttpServletRequest request) {
        try {
            // Recupera le tratte coinvolte
            Map<Long, Tratta> trattaMap = new HashMap<>();
            
            if (percorso.getSegmenti() != null) {
                for (var segmento : percorso.getSegmenti()) {
                    Tratta tratta = TrattaDAO.doRetrieveById(segmento.getId_tratta());
                    if (tratta != null) {
                        trattaMap.put(segmento.getId_tratta(), tratta);
                    }
                }
            }
            
            // Determina l'ID utente
            Long userId = null;
            if (!isGuest) {
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute("utente") != null) {
                    Utente utente = (Utente) session.getAttribute("utente");
                    userId = utente.getId();
                }
            }
            
            // Crea il biglietto
            Biglietto biglietto = new Biglietto(percorso, userId, trattaMap, Biglietto.StatoBiglietto.ACQUISTATO);
            return biglietto;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella creazione del biglietto", e);
            return null;
        }
    }
    
    /**
     * Salva il biglietto nel database
     */
    private Long saveBiglietto(Biglietto biglietto) {
        try {
            return BigliettiDAO.create(biglietto);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore nel salvataggio del biglietto", e);
            return null;
        }
    }
    
    /**
     * Salva il biglietto nella sessione per gli ospiti
     */
    private void saveGuestTicket(HttpServletRequest request, Biglietto biglietto) {
        HttpSession session = request.getSession(true);
        
        // Crea o recupera la lista dei biglietti ospite
        @SuppressWarnings("unchecked")
        Map<String, Biglietto> guestTickets = (Map<String, Biglietto>) session.getAttribute("guestTickets");
        
        if (guestTickets == null) {
            guestTickets = new HashMap<>();
        }
        
        // Genera un ID univoco per il biglietto ospite
        String guestTicketId = UUID.randomUUID().toString();
        guestTickets.put(guestTicketId, biglietto);
        
        session.setAttribute("guestTickets", guestTickets);
    }
    
    /**
     * Invia una risposta di errore
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        PaymentResponse errorResponse = new PaymentResponse();
        errorResponse.success = false;
        errorResponse.error = message;
        
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(errorResponse);
        
        response.getWriter().write(jsonResponse);
    }
    
    // Classe per la risposta JSON
    private static class PaymentResponse {
        boolean success;
        String error;
        String ticketId;
        String message;
    }
}
