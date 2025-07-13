package model.pathfinding;

import model.sdata.Fermata;
import model.sdata.Tratta;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Rappresenta un trasferimento tra due tratte in una fermata comune.
 */
public class Trasferimento {
    private Tratta trattaPartenza;
    private Tratta trattaArrivo;
    private Fermata fermataTransfer;
    private int tempoTrasferimento; // in minuti
    private boolean attivo;
    private double penalitaTrasferimento; // Penalità per il trasferimento (0.0 - 1.0)

    /**
     * Costruttore principale per un trasferimento.
     */
    public Trasferimento(Tratta trattaPartenza, Tratta trattaArrivo, Fermata fermataTransfer, int tempoTrasferimento) {
        this.trattaPartenza = trattaPartenza;
        this.trattaArrivo = trattaArrivo;
        this.fermataTransfer = fermataTransfer;
        this.tempoTrasferimento = tempoTrasferimento;
        this.attivo = true;
        this.penalitaTrasferimento = 0.1; // 10% di penalità di default
    }

    /**
     * Costruttore vuoto.
     */
    public Trasferimento() {
        this.attivo = true;
        this.penalitaTrasferimento = 0.1;
    }

    /**
     * Verifica se il trasferimento è fattibile dato un orario di arrivo e di partenza.
     */
    public boolean isValido(LocalTime orarioArrivo, LocalTime orarioPartenza) {
        if (orarioArrivo == null || orarioPartenza == null) {
            return false;
        }
        
        // Verifica che ci sia abbastanza tempo per il trasferimento
        LocalTime tempoMinimoPartenza = orarioArrivo.plusMinutes(tempoTrasferimento);
        return !orarioPartenza.isBefore(tempoMinimoPartenza);
    }

    /**
     * Calcola il tempo di attesa per il trasferimento.
     */
    public int calcolaTempoAttesa(LocalTime orarioArrivo, LocalTime orarioPartenza) {
        if (!isValido(orarioArrivo, orarioPartenza)) {
            return -1; // Trasferimento non valido
        }
        
        LocalTime tempoMinimoPartenza = orarioArrivo.plusMinutes(tempoTrasferimento);
        
        // Se l'orario di partenza è esattamente il tempo minimo, non c'è attesa aggiuntiva
        if (orarioPartenza.equals(tempoMinimoPartenza)) {
            return 0;
        }
        
        // Calcola l'attesa aggiuntiva
        return (int) java.time.Duration.between(tempoMinimoPartenza, orarioPartenza).toMinutes();
    }

    /**
     * Verifica se il trasferimento coinvolge le tratte specificate.
     */
    public boolean coinvolgeTratte(Tratta tratta1, Tratta tratta2) {
        return (trattaPartenza.equals(tratta1) && trattaArrivo.equals(tratta2)) ||
               (trattaPartenza.equals(tratta2) && trattaArrivo.equals(tratta1));
    }

    /**
     * Ottiene una descrizione leggibile del trasferimento.
     */
    public String getDescrizione() {
        StringBuilder sb = new StringBuilder();
        sb.append("Trasferimento da ");
        if (trattaPartenza != null) {
            sb.append(trattaPartenza.getNome());
        }
        sb.append(" a ");
        if (trattaArrivo != null) {
            sb.append(trattaArrivo.getNome());
        }
        sb.append(" presso ");
        if (fermataTransfer != null) {
            sb.append(fermataTransfer.getNome());
        }
        sb.append(" (").append(tempoTrasferimento).append(" min)");
        return sb.toString();
    }

    // Getters e Setters
    public Tratta getTrattaPartenza() { return trattaPartenza; }
    public void setTrattaPartenza(Tratta trattaPartenza) { this.trattaPartenza = trattaPartenza; }

    public Tratta getTrattaArrivo() { return trattaArrivo; }
    public void setTrattaArrivo(Tratta trattaArrivo) { this.trattaArrivo = trattaArrivo; }

    public Fermata getFermataTransfer() { return fermataTransfer; }
    public void setFermataTransfer(Fermata fermataTransfer) { this.fermataTransfer = fermataTransfer; }

    public int getTempoTrasferimento() { return tempoTrasferimento; }
    public void setTempoTrasferimento(int tempoTrasferimento) { this.tempoTrasferimento = tempoTrasferimento; }

    public boolean isAttivo() { return attivo; }
    public void setAttivo(boolean attivo) { this.attivo = attivo; }

    public double getPenalitaTrasferimento() { return penalitaTrasferimento; }
    public void setPenalitaTrasferimento(double penalitaTrasferimento) { this.penalitaTrasferimento = penalitaTrasferimento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trasferimento)) return false;
        Trasferimento that = (Trasferimento) o;
        return tempoTrasferimento == that.tempoTrasferimento &&
               attivo == that.attivo &&
               Double.compare(that.penalitaTrasferimento, penalitaTrasferimento) == 0 &&
               Objects.equals(trattaPartenza, that.trattaPartenza) &&
               Objects.equals(trattaArrivo, that.trattaArrivo) &&
               Objects.equals(fermataTransfer, that.fermataTransfer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trattaPartenza, trattaArrivo, fermataTransfer, tempoTrasferimento, attivo, penalitaTrasferimento);
    }

    @Override
    public String toString() {
        return "Trasferimento{" +
                "trattaPartenza=" + (trattaPartenza != null ? trattaPartenza.getNome() : "null") +
                ", trattaArrivo=" + (trattaArrivo != null ? trattaArrivo.getNome() : "null") +
                ", fermataTransfer=" + (fermataTransfer != null ? fermataTransfer.getNome() : "null") +
                ", tempoTrasferimento=" + tempoTrasferimento +
                ", attivo=" + attivo +
                ", penalitaTrasferimento=" + penalitaTrasferimento +
                '}';
    }
}
