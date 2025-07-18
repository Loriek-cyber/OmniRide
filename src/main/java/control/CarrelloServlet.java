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
import java.util.List;
import java.util.ArrayList;

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
        }
        
        session.setAttribute("carrello", carrello);
        response.sendRedirect(request.getContextPath() + "/carrello");
    }
    
    private void aggiungiAlCarrello(HttpServletRequest request, List<BigliettoCarrello> carrello) {
        try {
            String percorsoJson = request.getParameter("percorso");
            String data = request.getParameter("data");
            String orario = request.getParameter("orario");
            double prezzo = Double.parseDouble(request.getParameter("prezzo"));
            int quantita = Integer.parseInt(request.getParameter("quantita"));
            
            // Verifica se il biglietto è già nel carrello
            BigliettoCarrello existing = carrello.stream()
                .filter(bc -> bc.getData().equals(data) && 
                             bc.getOrario().equals(orario) && 
                             bc.getPrezzo() == prezzo)
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
    
    // Classe interna per rappresentare un biglietto nel carrello
    public static class BigliettoCarrello {
        private String percorsoJson;
        private String data;
        private String orario;
        private double prezzo;
        private int quantita;
        
        // Costruttori
        public BigliettoCarrello() {}
        
        // Getters e Setters
        public String getPercorsoJson() { return percorsoJson; }
        public void setPercorsoJson(String percorsoJson) { this.percorsoJson = percorsoJson; }
        
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        
        public String getOrario() { return orario; }
        public void setOrario(String orario) { this.orario = orario; }
        
        public double getPrezzo() { return prezzo; }
        public void setPrezzo(double prezzo) { this.prezzo = prezzo; }
        
        public int getQuantita() { return quantita; }
        public void setQuantita(int quantita) { this.quantita = quantita; }
    }
}
