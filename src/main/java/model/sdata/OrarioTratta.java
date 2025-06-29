package model.sdata;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class OrarioTratta {
    private Long trattaId;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private Set<DayOfWeek> giorniSettimana;
    private boolean attivo;

    public OrarioTratta() {
        this.giorniSettimana = new HashSet<>();
        this.attivo = true;
    }

    public Long getTrattaId() {return trattaId;}
    public void setTrattaId(Long trattaId) {this.trattaId = trattaId;}
    public LocalTime getOraInizio() {return oraInizio;}
    public void setOraInizio(LocalTime oraInizio) {this.oraInizio = oraInizio;}
    public LocalTime getOraFine() {return oraFine;}
    public void setOraFine(LocalTime oraFine) {this.oraFine = oraFine;}
    public Set<DayOfWeek> getGiorniSettimana() {return giorniSettimana;}
    public void setGiorniSettimana(Set<DayOfWeek> giorniSettimana) {this.giorniSettimana = giorniSettimana;}
    public boolean isAttivo() {return attivo;}
    public void setAttivo(boolean attivo) {this.attivo = attivo;}
}