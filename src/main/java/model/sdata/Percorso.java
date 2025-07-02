package model.sdata;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta un percorso completo calcolato dall'algoritmo di pathfinding.
 * Un percorso è una sequenza di tappe che collegano una fermata di partenza
 * a una di destinazione, potenzialmente includendo cambi di linea.
 */
public class Percorso {

    /**
     * Rappresenta una singola tappa (o segmento) del percorso.
     * Corrisponde a un viaggio su una specifica Tratta tra due Fermate.
     */
    public static class Tappa {
        private final Tratta tratta;
        private final Fermata fermataPartenza;
        private final Fermata fermataArrivo;
        private final LocalTime orarioPartenza;
        private final LocalTime orarioArrivo;

        public Tappa(Tratta tratta, Fermata fermataPartenza, Fermata fermataArrivo, LocalTime orarioPartenza, LocalTime orarioArrivo) {
            this.tratta = tratta;
            this.fermataPartenza = fermataPartenza;
            this.fermataArrivo = fermataArrivo;
            this.orarioPartenza = orarioPartenza;
            this.orarioArrivo = orarioArrivo;
        }

        // Getters
        public Tratta getTratta() { return tratta; }
        public Fermata getFermataPartenza() { return fermataPartenza; }
        public Fermata getFermataArrivo() { return fermataArrivo; }
        public LocalTime getOrarioPartenza() { return orarioPartenza; }
        public LocalTime getOrarioArrivo() { return orarioArrivo; }
        public Duration getDurata() {
            return Duration.between(orarioPartenza, orarioArrivo);
        }
    }

    private final List<Tappa> tappe;
    private Duration durataTotale;
    private LocalTime orarioPartenzaComplessivo;
    private LocalTime orarioArrivoComplessivo;

    public Percorso(List<Tappa> tappe) {
        this.tappe = new ArrayList<>(tappe);
        ricalcolaDatiComplessivi();
    }

    /**
     * Ricalcola i dati riassuntivi del percorso (durata, orari)
     * basandosi sulla lista di tappe.
     */
    private void ricalcolaDatiComplessivi() {
        if (tappe.isEmpty()) {
            this.durataTotale = Duration.ZERO;
            this.orarioPartenzaComplessivo = null;
            this.orarioArrivoComplessivo = null;
            return;
        }

        // L'orario di partenza è quello della prima tappa
        this.orarioPartenzaComplessivo = tappe.get(0).getOrarioPartenza();
        // L'orario di arrivo è quello dell'ultima tappa
        this.orarioArrivoComplessivo = tappe.get(tappe.size() - 1).getOrarioArrivo();

        // La durata totale è la differenza tra l'arrivo finale e la partenza iniziale
        if (this.orarioPartenzaComplessivo != null && this.orarioArrivoComplessivo != null) {
            this.durataTotale = Duration.between(this.orarioPartenzaComplessivo, this.orarioArrivoComplessivo);
        }
    }

    // --- Getters per i dati del percorso ---

    public List<Tappa> getTappe() {
        return Collections.unmodifiableList(tappe);
    }

    public Duration getDurataTotale() {
        return durataTotale;
    }

    public LocalTime getOrarioPartenzaComplessivo() {
        return orarioPartenzaComplessivo;
    }

    public LocalTime getOrarioArrivoComplessivo() {
        return orarioArrivoComplessivo;
    }

    public Fermata getFermataIniziale() {
        return tappe.isEmpty() ? null : tappe.get(0).getFermataPartenza();
    }

    public Fermata getFermataFinale() {
        return tappe.isEmpty() ? null : tappe.get(tappe.size() - 1).getFermataArrivo();
    }

    /**
     * Calcola il numero di cambi di linea necessari per completare il percorso.
     * @return Il numero di cambi.
     */
    public int getNumeroCambi() {
        if (tappe.size() <= 1) {
            return 0;
        }
        int cambi = 0;
        // Un cambio avviene quando l'ID della tratta della tappa successiva è diverso da quello attuale
        for (int i = 0; i < tappe.size() - 1; i++) {
            Long idTrattaCorrente = tappe.get(i).getTratta().getId();
            Long idTrattaSuccessiva = tappe.get(i + 1).getTratta().getId();
            if (!idTrattaCorrente.equals(idTrattaSuccessiva)) {
                cambi++;
            }
        }
        return cambi;
    }
}