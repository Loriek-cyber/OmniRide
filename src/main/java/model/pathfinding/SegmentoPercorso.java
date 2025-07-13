package model.pathfinding;

import model.sdata.Fermata;
import model.sdata.OrarioTratta;
import model.sdata.Tratta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Rappresenta un segmento del percorso, ovvero una porzione di viaggio
 * su una singola tratta tra due fermate specifiche.
 */
public class SegmentoPercorso implements Serializable {
    
    public enum TipoSegmento {
        DIRETTO("Viaggio diretto"),
        TRASFERIMENTO("Trasferimento"),
        ATTESA("Tempo di attesa");
        
        private final String descrizione;
        
        TipoSegmento(String descrizione) {
            this.descrizione = descrizione;
        }
        
        public String getDescrizione() {
            return descrizione;
        }
    }
    
    private Long id;
    private Tratta tratta;
    private Fermata fermataPartenza;
    private Fermata fermataArrivo;
    private OrarioTratta orarioUtilizzato;
    private LocalTime orarioPartenzaSegmento;
    private LocalTime orarioArrivoSegmento;
    private BigDecimal costoSegmento;
    private Duration durataSegmento;
    private int sequenza; // Ordine nel percorso
    private TipoSegmento tipo;
    
    // Costruttori
    public SegmentoPercorso() {
        this.tipo = TipoSegmento.DIRETTO;
        this.costoSegmento = BigDecimal.ZERO;
    }
    
    public SegmentoPercorso(Tratta tratta, Fermata fermataPartenza, Fermata fermataArrivo, 
                           OrarioTratta orarioUtilizzato, int sequenza) {
        this();
        this.tratta = tratta;
        this.fermataPartenza = fermataPartenza;
        this.fermataArrivo = fermataArrivo;
        this.orarioUtilizzato = orarioUtilizzato;
        this.sequenza = sequenza;
        
        // Calcola automaticamente costo e durata
        calcolaCostoSegmento();
        calcolaDurataSegmento();
    }
    
    /**
     * Calcola il costo di questo segmento del percorso
     */
    public void calcolaCostoSegmento() {
        if (tratta != null) {
            // Per ora usa il costo completo della tratta
            // In futuro si potrebbe implementare un calcolo proporzionale
            this.costoSegmento = BigDecimal.valueOf(tratta.getCosto());
        } else {
            this.costoSegmento = BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcola la durata di questo segmento del percorso
     */
    public void calcolaDurataSegmento() {
        if (tratta != null && fermataPartenza != null && fermataArrivo != null) {
            try {
                long minutiViaggio = tratta.getDistanceForTwoFermate(fermataPartenza, fermataArrivo);
                this.durataSegmento = Duration.ofMinutes(minutiViaggio);
                
                // Calcola orari specifici se disponibili
                if (orarioUtilizzato != null) {
                    this.orarioPartenzaSegmento = orarioUtilizzato.getOraPartenza().toLocalTime();
                    this.orarioArrivoSegmento = orarioPartenzaSegmento.plus(durataSegmento);
                }
            } catch (Exception e) {
                this.durataSegmento = Duration.ZERO;
            }
        } else {
            this.durataSegmento = Duration.ZERO;
        }
    }
    
    /**
     * Verifica se questo segmento è valido
     */
    public boolean isValido() {
        return tratta != null && 
               fermataPartenza != null && 
               fermataArrivo != null && 
               !fermataPartenza.equals(fermataArrivo) &&
               orarioUtilizzato != null &&
               durataSegmento != null && 
               !durataSegmento.isNegative();
    }
    
    /**
     * Ottiene una descrizione leggibile del segmento
     */
    public String getDescrizione() {
        if (tratta == null || fermataPartenza == null || fermataArrivo == null) {
            return "Segmento non valido";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Tratta: ").append(tratta.getNome())
          .append(" - Da: ").append(fermataPartenza.getNome())
          .append(" A: ").append(fermataArrivo.getNome());
        
        if (orarioPartenzaSegmento != null) {
            sb.append(" (").append(orarioPartenzaSegmento).append(")");
        }
        
        return sb.toString();
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Tratta getTratta() { return tratta; }
    public void setTratta(Tratta tratta) { 
        this.tratta = tratta; 
        calcolaCostoSegmento();
        calcolaDurataSegmento();
    }
    
    public Fermata getFermataPartenza() { return fermataPartenza; }
    public void setFermataPartenza(Fermata fermataPartenza) { 
        this.fermataPartenza = fermataPartenza; 
        calcolaDurataSegmento();
    }
    
    public Fermata getFermataArrivo() { return fermataArrivo; }
    public void setFermataArrivo(Fermata fermataArrivo) { 
        this.fermataArrivo = fermataArrivo; 
        calcolaDurataSegmento();
    }
    
    public OrarioTratta getOrarioUtilizzato() { return orarioUtilizzato; }
    public void setOrarioUtilizzato(OrarioTratta orarioUtilizzato) { 
        this.orarioUtilizzato = orarioUtilizzato; 
        calcolaDurataSegmento();
    }
    
    public LocalTime getOrarioPartenzaSegmento() { return orarioPartenzaSegmento; }
    public void setOrarioPartenzaSegmento(LocalTime orarioPartenzaSegmento) { 
        this.orarioPartenzaSegmento = orarioPartenzaSegmento; 
    }
    
    public LocalTime getOrarioArrivoSegmento() { return orarioArrivoSegmento; }
    public void setOrarioArrivoSegmento(LocalTime orarioArrivoSegmento) { 
        this.orarioArrivoSegmento = orarioArrivoSegmento; 
    }
    
    public BigDecimal getCostoSegmento() { return costoSegmento; }
    public void setCostoSegmento(BigDecimal costoSegmento) { this.costoSegmento = costoSegmento; }
    
    public Duration getDurataSegmento() { return durataSegmento; }
    public void setDurataSegmento(Duration durataSegmento) { this.durataSegmento = durataSegmento; }
    
    public int getSequenza() { return sequenza; }
    public void setSequenza(int sequenza) { this.sequenza = sequenza; }
    
    public TipoSegmento getTipo() { return tipo; }
    public void setTipo(TipoSegmento tipo) { this.tipo = tipo; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SegmentoPercorso)) return false;
        SegmentoPercorso that = (SegmentoPercorso) o;
        return sequenza == that.sequenza &&
               Objects.equals(tratta, that.tratta) &&
               Objects.equals(fermataPartenza, that.fermataPartenza) &&
               Objects.equals(fermataArrivo, that.fermataArrivo) &&
               Objects.equals(orarioUtilizzato, that.orarioUtilizzato);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tratta, fermataPartenza, fermataArrivo, orarioUtilizzato, sequenza);
    }
    
    @Override
    public String toString() {
        return "SegmentoPercorso{" +
                "tratta=" + (tratta != null ? tratta.getNome() : "null") +
                ", fermataPartenza=" + (fermataPartenza != null ? fermataPartenza.getNome() : "null") +
                ", fermataArrivo=" + (fermataArrivo != null ? fermataArrivo.getNome() : "null") +
                ", orarioPartenza=" + orarioPartenzaSegmento +
                ", orarioArrivo=" + orarioArrivoSegmento +
                ", costo=" + costoSegmento +
                ", durata=" + durataSegmento +
                ", sequenza=" + sequenza +
                ", tipo=" + tipo +
                '}';
    }
}
