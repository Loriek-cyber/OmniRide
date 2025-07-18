package model.sdata;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class OrarioTratta{
    private Long id;
    private Long trattaId;
    private Time oraPartenza;
    private Time oraArrivo;
    private String giorniSettimana; // Formato: "LUN,MAR,MER,GIO,VEN"
    private List<LocalTime> listatime;
    private boolean attivo;
    private String note;
    


    // Costruttori
    public OrarioTratta() {
        this.attivo = true;
    }
    
    public OrarioTratta(Long trattaId, Time oraPartenza, String giorniSettimana) {
        this();
        this.trattaId = trattaId;
        this.oraPartenza = oraPartenza;
        this.giorniSettimana = giorniSettimana;
    }
    
    public OrarioTratta(Long id, Long trattaId, Time oraPartenza, Time oraArrivo,
                       String giorniSettimana, boolean attivo, String note) {
        this.id = id;
        this.trattaId = trattaId;
        this.oraPartenza = oraPartenza;
        this.oraArrivo = oraArrivo;
        this.giorniSettimana = giorniSettimana;
        this.attivo = attivo;
        this.note = note;
    }
    
    public List<String> getGiorniAsArray() {
        if (giorniSettimana == null || giorniSettimana.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(giorniSettimana.split(","));
    }

    public List<LocalTime> getListatime() {
        return listatime;
    }

    public void setListatime(List<LocalTime> listatime) {
        this.listatime = listatime;
    }

    /**
     * Verifica se l'orario è valido per un determinato giorno
     */
    public boolean isValidoPerGiorno(String giorno) {
        return giorniSettimana != null && giorniSettimana.contains(giorno.toUpperCase());
    }

    /*Questa sezione e per non cambiare troppo il database*/

    public void addnext(int minuti){
        if(listatime == null){
            listatime = new ArrayList<LocalTime>();
            // Aggiungi l'orario di partenza iniziale se la lista è vuota
            LocalTime ldt = LocalTime.parse(oraPartenza.toString());
            listatime.add(ldt);
        }
        
        // Prende l'ultimo orario nella lista e aggiunge i minuti
        LocalTime ultimoOrario = listatime.get(listatime.size() - 1);
        LocalTime nuovoOrario = ultimoOrario.plusMinutes(minuti);
        listatime.add(nuovoOrario);
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
    
    public Time getOraPartenza() { return oraPartenza; }
    public void setOraPartenza(Time oraPartenza) { this.oraPartenza = oraPartenza; }
    
    public Time getOraArrivo() { 
        if (listatime == null || listatime.isEmpty()) {
            return oraArrivo;
        }
        // Usa get(size-1) invece di getLast() per compatibilità
        return Time.valueOf(listatime.get(listatime.size() - 1));
    }
    public void setOraArrivo(Time oraArrivo) { this.oraArrivo = oraArrivo; }
    
    public String getGiorniSettimana() { return giorniSettimana; }
    public void setGiorniSettimana(String giorniSettimana) { this.giorniSettimana = giorniSettimana; }

    public boolean isAttivo() { return attivo; }
    public void setAttivo(boolean attivo) { this.attivo = attivo; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    // Override dei metodi Object
    @Override
    public String toString() {
        return "OrarioTratta{" +
                ", trattaId=" + trattaId +
                ", oraPartenza=" + (oraPartenza != null ? oraPartenza.toString() : "null") +
                ", oraArrivo=" + (oraArrivo != null ? oraArrivo.toString() : "null") +
                ", giorniSettimana='" + giorniSettimana + '\'' +
                ", attivo=" + attivo +
                ", note='" + note + '\'' +
                '}';
    }
}
