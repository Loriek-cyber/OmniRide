package data;

import java.time.LocalDateTime;

public class Fermata {
    
    private String idFermata;
    private String indirizzo;
    private LocalDateTime orario;
    private double latitudine;
    private double longitudine;
    
    public Fermata(String idFermata, String indirizzo, LocalDateTime orario, double latitudine, double longitudine) {
        this.idFermata = idFermata;
        this.indirizzo = indirizzo;
        this.orario = orario;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }
    
    public String getIdFermata() {
        return idFermata;
    }
    
    public double getLatitudine() {
        return latitudine;
    }
    
    public double getLongitudine() {
        return longitudine;
    }
    
    public String getNome() {
        return indirizzo; // Supponiamo che il nome sia l'indirizzo per semplicità
    }
    
    public LocalDateTime getOrario() {
        return orario;
    }
    
    @Override
    public String toString() {
        return "Fermata [idFermata=" + idFermata 
                + ", indirizzo=" + indirizzo 
                + ", orario=" + orario 
                + ", latitudine=" + latitudine 
                + ", longitudine=" + longitudine + "]";
    }
}