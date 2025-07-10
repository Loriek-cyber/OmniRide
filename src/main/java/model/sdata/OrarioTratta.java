package model.sdata;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OrarioTratta{
    
    private Long id;
    private Long trattaId;
    private LocalTime oraPartenza;
    private LocalTime oraArrivo;
    private String giorniSettimana; // Formato: "LUN,MAR,MER,GIO,VEN"
    private TipoServizio tipoServizio;
    private boolean attivo;
    private String note;
    
    public enum TipoServizio {
        NORMALE("Servizio Normale"),
        FESTIVO("Servizio Festivo"),
        NOTTURNO("Servizio Notturno"),
        EXPRESS("Servizio Express"),
        TURISTICO("Servizio Turistico");

        private final String descrizione;
        
        TipoServizio(String descrizione) {
            this.descrizione = descrizione;
        }
        
        public String getDescrizione() {
            return descrizione;
        }
    }
    
    // Costruttori
    public OrarioTratta() {
        this.attivo = true;
        this.tipoServizio = TipoServizio.NORMALE;
    }
    
    public OrarioTratta(Long trattaId, LocalTime oraPartenza, String giorniSettimana) {
        this();
        this.trattaId = trattaId;
        this.oraPartenza = oraPartenza;
        this.giorniSettimana = giorniSettimana;
    }
    
    public OrarioTratta(Long id, Long trattaId, LocalTime oraPartenza, LocalTime oraArrivo, 
                       String giorniSettimana, TipoServizio tipoServizio, boolean attivo, String note) {
        this.id = id;
        this.trattaId = trattaId;
        this.oraPartenza = oraPartenza;
        this.oraArrivo = oraArrivo;
        this.giorniSettimana = giorniSettimana;
        this.tipoServizio = tipoServizio;
        this.attivo = attivo;
        this.note = note;
    }
    
    // Metodi di utilità
    
    /**
     * Ottiene la lista dei giorni della settimana come array
     */
    public List<String> getGiorniAsArray() {
        if (giorniSettimana == null || giorniSettimana.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(giorniSettimana.split(","));
    }
    
    /**
     * Verifica se l'orario è valido per un determinato giorno
     */
    public boolean isValidoPerGiorno(String giorno) {
        return giorniSettimana != null && giorniSettimana.contains(giorno.toUpperCase());
    }
    
    /**
     * Calcola l'orario di arrivo basato sul tempo di percorrenza totale
     */
    public void calcolaOraArrivo(int minutiPercorrenza) {
        if (oraPartenza != null) {
            this.oraArrivo = oraPartenza.plusMinutes(minutiPercorrenza);
        }
    }
    
    /**
     * Formatta l'orario per la visualizzazione
     */
    public String getOrarioFormattato() {
        StringBuilder sb = new StringBuilder();
        if (oraPartenza != null) {
            sb.append(oraPartenza.toString());
        }
        if (oraArrivo != null) {
            sb.append(" - ").append(oraArrivo.toString());
        }
        return sb.toString();
    }
    
    /**
     * Ottiene una descrizione leggibile dei giorni
     */
    public String getGiorniDescrizione() {
        if (giorniSettimana == null || giorniSettimana.isEmpty()) {
            return "Nessun giorno specificato";
        }
        
        // Conversione giorni abbreviati in nomi completi
        String giorni = giorniSettimana
            .replace("LUN", "Lunedì")
            .replace("MAR", "Martedì")
            .replace("MER", "Mercoledì")
            .replace("GIO", "Giovedì")
            .replace("VEN", "Venerdì")
            .replace("SAB", "Sabato")
            .replace("DOM", "Domenica");
            
        return giorni.replace(",", ", ");
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getTrattaId() { return trattaId; }
    public void setTrattaId(Long trattaId) { this.trattaId = trattaId; }
    
    public LocalTime getOraPartenza() { return oraPartenza; }
    public void setOraPartenza(LocalTime oraPartenza) { this.oraPartenza = oraPartenza; }
    
    public LocalTime getOraArrivo() { return oraArrivo; }
    public void setOraArrivo(LocalTime oraArrivo) { this.oraArrivo = oraArrivo; }
    
    public String getGiorniSettimana() { return giorniSettimana; }
    public void setGiorniSettimana(String giorniSettimana) { this.giorniSettimana = giorniSettimana; }
    
    public TipoServizio getTipoServizio() { return tipoServizio; }
    public void setTipoServizio(TipoServizio tipoServizio) { this.tipoServizio = tipoServizio; }
    
    public boolean isAttivo() { return attivo; }
    public void setAttivo(boolean attivo) { this.attivo = attivo; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    // Override dei metodi Object
    @Override
    public String toString() {
        return "OrarioTratta{" +
                "id=" + id +
                ", trattaId=" + trattaId +
                ", oraPartenza=" + (oraPartenza != null ? oraPartenza.toString() : "null") +
                ", oraArrivo=" + (oraArrivo != null ? oraArrivo.toString() : "null") +
                ", giorniSettimana='" + giorniSettimana + '\'' +
                ", tipoServizio=" + tipoServizio +
                ", attivo=" + attivo +
                ", note='" + note + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrarioTratta that = (OrarioTratta) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
