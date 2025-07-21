package model;

import model.udata.Biglietto;
import java.util.ArrayList;
import java.util.List;

public class Carello {
    private List<ItemCarrello> items;
    private double totale;
    
    public Carello() {
        this.items = new ArrayList<>();
        this.totale = 0.0;
    }
    
    public void aggiungiItem(ItemCarrello item) {
        // Verifica se l'item è già presente
        ItemCarrello existing = items.stream()
            .filter(i -> i.equals(item))
            .findFirst()
            .orElse(null);
            
        if (existing != null) {
            existing.setQuantita(existing.getQuantita() + item.getQuantita());
        } else {
            items.add(item);
        }
        
        calcolaTotale();
    }
    
    public void rimuoviItem(int indice) {
        if (indice >= 0 && indice < items.size()) {
            items.remove(indice);
            calcolaTotale();
        }
    }
    
    public void aggiornaQuantita(int indice, int nuovaQuantita) {
        if (indice >= 0 && indice < items.size() && nuovaQuantita > 0) {
            items.get(indice).setQuantita(nuovaQuantita);
            calcolaTotale();
        }
    }
    
    public void svuota() {
        items.clear();
        totale = 0.0;
    }
    
    private void calcolaTotale() {
        totale = items.stream()
            .mapToDouble(item -> item.getPrezzo() * item.getQuantita())
            .sum();
    }
    
    public int getTotaleBiglietti() {
        return items.stream()
            .mapToInt(ItemCarrello::getQuantita)
            .sum();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    // Getters e Setters
    public List<ItemCarrello> getItems() {
        return items;
    }
    
    public void setItems(List<ItemCarrello> items) {
        this.items = items;
        calcolaTotale();
    }
    
    public double getTotale() {
        return totale;
    }
    
    public void setTotale(double totale) {
        this.totale = totale;
    }
    
    // Classe interna per rappresentare un item del carrello
    public static class ItemCarrello {
        private String percorsoJson;
        private String nome;
        private String data;
        private String orario;
        private double prezzo;
        private int quantita;
        private Biglietto.TipoBiglietto tipo;
        
        public ItemCarrello() {
            this.tipo = Biglietto.TipoBiglietto.NORMALE;
        }
        
        public ItemCarrello(String percorsoJson, String nome, String data, String orario, 
                          double prezzo, int quantita, Biglietto.TipoBiglietto tipo) {
            this.percorsoJson = percorsoJson;
            this.nome = nome;
            this.data = data;
            this.orario = orario;
            this.prezzo = prezzo;
            this.quantita = quantita;
            this.tipo = tipo != null ? tipo : Biglietto.TipoBiglietto.NORMALE;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            
            ItemCarrello that = (ItemCarrello) obj;
            return Double.compare(that.prezzo, prezzo) == 0 &&
                   data.equals(that.data) &&
                   orario.equals(that.orario) &&
                   tipo == that.tipo &&
                   (percorsoJson != null ? percorsoJson.equals(that.percorsoJson) : that.percorsoJson == null);
        }
        
        @Override
        public int hashCode() {
            int result = percorsoJson != null ? percorsoJson.hashCode() : 0;
            result = 31 * result + data.hashCode();
            result = 31 * result + orario.hashCode();
            result = 31 * result + Double.hashCode(prezzo);
            result = 31 * result + tipo.hashCode();
            return result;
        }
        
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
        
        public Biglietto.TipoBiglietto getTipo() { return tipo; }
        public void setTipo(Biglietto.TipoBiglietto tipo) { this.tipo = tipo; }
        
        public String getPercorso() { return nome; } // Per compatibilità con la JSP
    }
}
