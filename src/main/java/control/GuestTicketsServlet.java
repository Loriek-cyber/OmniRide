package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.BigliettiDAO;
import model.udata.Biglietto;
import model.udata.Utente;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/guest-tickets")
public class GuestTicketsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        if ("get".equals(action)) {
            // Restituisce i biglietti guest dalla sessione
            @SuppressWarnings("unchecked")
            List<Biglietto> guestTickets = (List<Biglietto>) session.getAttribute("guestTickets");
            
            if (guestTickets == null) {
                guestTickets = new ArrayList<>();
            }
            
            // Debug: stampa gli ID dei biglietti guest
            System.out.println("[GUEST_TICKETS DEBUG] Trovati " + guestTickets.size() + " biglietti guest nella sessione:");
            for (Biglietto biglietto : guestTickets) {
                System.out.println("[GUEST_TICKETS DEBUG] - Biglietto ID: " + biglietto.getId() + ", Tipo: " + biglietto.getTipo() + ", Prezzo: " + biglietto.getPrezzo());
            }
            
            Gson gson = new Gson();
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.add("tickets", gson.toJsonTree(guestTickets));
            
            out.print(gson.toJson(jsonResponse));
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        
        if ("save".equals(action)) {
            // Salva un nuovo biglietto guest nella sessione
            String ticketDataJson = request.getParameter("ticketData");
            
            if (ticketDataJson != null && !ticketDataJson.isEmpty()) {
                try {
                    // Parse del JSON del biglietto
                    Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
                    Map<String, Object> ticketData = gson.fromJson(ticketDataJson, mapType);
                    
                    // Crea un nuovo biglietto
                    Biglietto biglietto = new Biglietto();
                    biglietto.setId_utente(-1L); // ID speciale per guest
                    biglietto.setNome((String) ticketData.get("percorso"));
                    biglietto.setCodiceBiglietto((String) ticketData.get("codice"));
                    biglietto.setPrezzo(((Number) ticketData.get("prezzo")).doubleValue());
                    biglietto.setDataAcquisto(LocalTime.now());
                    biglietto.setStato(Biglietto.StatoBiglietto.ACQUISTATO);
                    
                    // Parse del tipo biglietto
                    String tipoStr = (String) ticketData.get("tipo");
                    Biglietto.TipoBiglietto tipo;
                    try {
                        tipo = Biglietto.TipoBiglietto.valueOf(tipoStr);
                    } catch (Exception e) {
                        tipo = Biglietto.TipoBiglietto.NORMALE;
                    }
                    biglietto.setTipo(tipo);
                    
                    // Ottieni la lista dei biglietti guest dalla sessione
                    @SuppressWarnings("unchecked")
                    List<Biglietto> guestTickets = (List<Biglietto>) session.getAttribute("guestTickets");
                    
                    if (guestTickets == null) {
                        guestTickets = new ArrayList<>();
                        session.setAttribute("guestTickets", guestTickets);
                    }
                    
                    // Aggiungi il biglietto alla lista
                    guestTickets.add(biglietto);
                    
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Biglietto salvato nella sessione");
                    
                    out.print(gson.toJson(jsonResponse));
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "Errore nel salvataggio del biglietto: " + e.getMessage());
                    
                    out.print(gson.toJson(jsonResponse));
                }
            }
        } else if ("claim".equals(action)) {
            // Reclama i biglietti guest per un utente loggato
            Utente utente = (Utente) session.getAttribute("utente");
            
            if (utente != null) {
                try {
                    @SuppressWarnings("unchecked")
                    List<Biglietto> guestTickets = (List<Biglietto>) session.getAttribute("guestTickets");
                    
                    int claimedTickets = 0;
                    
                    if (guestTickets != null && !guestTickets.isEmpty()) {
                        // Estrai gli ID dei biglietti guest che hanno un ID nel database
                        List<Long> guestTicketIds = new ArrayList<>();
                        for (Biglietto biglietto : guestTickets) {
                            if (biglietto.getId() != null) {
                                guestTicketIds.add(biglietto.getId());
                            } else {
                                // Se il biglietto non ha un ID, crealo prima nel database
                                biglietto.setId_utente(-1L); // Temporaneamente assegnato a guest
                                
                                // Imposta valori di default per le liste se sono nulle
                                if (biglietto.getId_tratte() == null) {
                                    biglietto.setId_tratte(new ArrayList<>());
                                }
                                if (biglietto.getNumero_fermate() == null) {
                                    biglietto.setNumero_fermate(new ArrayList<>());
                                }
                                
                                Long bigliettoId = BigliettiDAO.create(biglietto);
                                if (bigliettoId != null) {
                                    guestTicketIds.add(bigliettoId);
                                }
                            }
                        }
                        
                        // Usa la funzione del DAO per reclamare i biglietti
                        claimedTickets = BigliettiDAO.claimGuestTickets(utente.getId(), guestTicketIds);
                        
                        // Svuota i biglietti guest dalla sessione
                        session.removeAttribute("guestTickets");
                    }
                    
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("claimedTickets", claimedTickets);
                    jsonResponse.addProperty("message", 
                        claimedTickets > 0 ? 
                        "Hai reclamato " + claimedTickets + " biglietti!" :
                        "Nessun biglietto da reclamare.");
                    
                    out.print(gson.toJson(jsonResponse));
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "Errore nel reclamare i biglietti: " + e.getMessage());
                    
                    out.print(gson.toJson(jsonResponse));
                }
            } else {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Utente non loggato");
                
                out.print(gson.toJson(jsonResponse));
            }
        }
        
        out.flush();
    }
}
