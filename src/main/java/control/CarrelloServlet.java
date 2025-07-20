package control;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import model.dao.TrattaDAO;
import model.dao.udata.SessioneDAO;
import model.dao.Carta_CreditoDAO;
import model.sdata.Tratta;
import model.udata.Biglietto;
import model.udata.CartaCredito;
import model.udata.Utente;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Recupera i biglietti dal carrello della sessione
        @SuppressWarnings("unchecked")
        List<BigliettoCarrello> carrello = (List<BigliettoCarrello>) session.getAttribute("carrello");
        
        if (carrello == null) {
            carrello = new ArrayList<>();
            session.setAttribute("carrello", carrello);
        }
        
        // Calcola il totale
        double totale = carrello.stream()
                .mapToDouble(bc -> bc.getPrezzo() * bc.getQuantita())
                .sum();
        
        // Non è più necessario caricare le carte di credito - il pagamento è semplificato
        // Manteniamo compatibilità con vecchio sistema se necessario
        request.setAttribute("carteCredito", new ArrayList<CartaCredito>());
        
        request.setAttribute("carrello", carrello);
        request.setAttribute("totale", totale);
        request.getRequestDispatcher("/carrello.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        @SuppressWarnings("unchecked")
        List<BigliettoCarrello> carrello = (List<BigliettoCarrello>) session.getAttribute("carrello");
        
        if (carrello == null) {
            carrello = new ArrayList<>();
            session.setAttribute("carrello", carrello);
        }
        
switch (action) {
            case "aggiungi":
                aggiungiAlCarrello(request, carrello);
                break;
            case "rimuovi":
                rimuoviDalCarrello(request, carrello);
                break;
            case "aggiorna":
                aggiornaQuantita(request, carrello);
                break;
            case "svuota":
                carrello.clear();
                break;
            case "sync":
                sincronizzaCarrello(request, carrello);
                break;
        }
        
        session.setAttribute("carrello", carrello);
        response.sendRedirect(request.getContextPath() + "/carrello");
    }
    
    private void aggiungiAlCarrello(HttpServletRequest request, List<BigliettoCarrello> carrello) {
        try {
            String percorsoJson = request.getParameter("percorso");
            String data = request.getParameter("data");
            String orario = request.getParameter("orario");
            String tipoStr = request.getParameter("tipo");
            double prezzo = Double.parseDouble(request.getParameter("prezzo"));
            int quantita = Integer.parseInt(request.getParameter("quantita"));
            
            final Biglietto.TipoBiglietto tipo = (tipoStr != null && !tipoStr.isEmpty()) 
                ? parseTipoBiglietto(tipoStr) 
                : Biglietto.TipoBiglietto.NORMALE;
            
            // Verifica se il biglietto è già nel carrello
            BigliettoCarrello existing = carrello.stream()
                .filter(bc -> bc.getData().equals(data) && 
                             bc.getOrario().equals(orario) && 
                             bc.getPrezzo() == prezzo &&
                             bc.getTipo() == tipo)
                .findFirst()
                .orElse(null);
            
            if (existing != null) {
                // Aggiorna la quantità
                existing.setQuantita(existing.getQuantita() + quantita);
            } else {
                // Aggiungi nuovo biglietto
                BigliettoCarrello nuovoBiglietto = new BigliettoCarrello();
                nuovoBiglietto.setPercorsoJson(percorsoJson);
                nuovoBiglietto.setData(data);
                nuovoBiglietto.setOrario(orario);
                nuovoBiglietto.setPrezzo(prezzo);
                nuovoBiglietto.setQuantita(quantita);
                nuovoBiglietto.setTipo(tipo);
                
// Costruisci il nome leggibile dal percorsoJson
                String nome = "Percorso Personalizzato";
                if (percorsoJson != null && !percorsoJson.isEmpty()) {
                    try {
                        // Se il percorsoJson contiene informazioni leggibili, usa quelle
                        if (percorsoJson.contains("-")) {
                            nome = percorsoJson.replace("[", "").replace("]", "").replace("\"", "");
                        }
                    } catch (Exception e) {
                        // Fallback al nome di default
                        nome = "Percorso Personalizzato";
                    }
                }
                nuovoBiglietto.setNome(nome);
                nuovoBiglietto.setPercorsoJson(percorsoJson);
                
                carrello.add(nuovoBiglietto);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void rimuoviDalCarrello(HttpServletRequest request, List<BigliettoCarrello> carrello) {
        try {
            int indice = Integer.parseInt(request.getParameter("indice"));
            if (indice >= 0 && indice < carrello.size()) {
                carrello.remove(indice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void aggiornaQuantita(HttpServletRequest request, List<BigliettoCarrello> carrello) {
        try {
            int indice = Integer.parseInt(request.getParameter("indice"));
            int nuovaQuantita = Integer.parseInt(request.getParameter("quantita"));
            
            if (indice >= 0 && indice < carrello.size() && nuovaQuantita > 0) {
                carrello.get(indice).setQuantita(nuovaQuantita);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Biglietto.TipoBiglietto parseTipoBiglietto(String tipoStr) {
        try {
            return Biglietto.TipoBiglietto.valueOf(tipoStr);
        } catch (IllegalArgumentException e) {
            return Biglietto.TipoBiglietto.NORMALE;
        }
    }
    
private void sincronizzaCarrello(HttpServletRequest request, List<BigliettoCarrello> carrello) {
        try {
            String cartData = request.getParameter("cartData");
            if (cartData != null && !cartData.isEmpty()) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<BigliettoCarrello>>(){}.getType();
                List<BigliettoCarrello> cookieCarrello = gson.fromJson(URLDecoder.decode(cartData, StandardCharsets.UTF_8.name()), listType);

                for (BigliettoCarrello item : cookieCarrello) {
                    boolean exists = false;
                    for (BigliettoCarrello existingItem : carrello) {
                        if (existingItem.getPercorsoJson().equals(item.getPercorsoJson()) &&
                            existingItem.getData().equals(item.getData()) &&
                            existingItem.getOrario().equals(item.getOrario()) &&
                            existingItem.getTipo() == item.getTipo() &&
                            existingItem.getPrezzo() == item.getPrezzo()) {
                            existingItem.setQuantita(existingItem.getQuantita() + item.getQuantita());
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        carrello.add(item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Classe interna per rappresentare un biglietto nel carrello
    public static class BigliettoCarrello {
        private String percorsoJson;
        private String nome;
        private String data;
        private String orario;
        private double prezzo;
        private int quantita;
        private Biglietto.TipoBiglietto tipo;
        
        // Costruttori
        public BigliettoCarrello() {
            this.tipo = Biglietto.TipoBiglietto.NORMALE; // Default type
        }
        
        public String getPercorso() { return nome; }
        // Getters e Setters
        public String getPercorsoJson() { return percorsoJson; }
        public void setPercorsoJson(String percorsoJson) { this.percorsoJson = percorsoJson; }
        
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        
        public String getOrario() { return orario; }
        public void setOrario(String orario) { this.orario = orario; }
        
        public double getPrezzo() { return prezzo; }
        public void setPrezzo(double prezzo) { this.prezzo = prezzo; }
        
        public int getQuantita() { return quantita; }
        public void setQuantita(int quantita) { this.quantita = quantita; }
        
        public model.udata.Biglietto.TipoBiglietto getTipo() { return tipo; }
        public void setTipo(model.udata.Biglietto.TipoBiglietto tipo) { this.tipo = tipo; }
    }
}
