package model.pathfinding;

import model.sdata.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

/**
 * Motore di ricerca percorsi basato su una versione dell'algoritmo di Dijkstra
 * ottimizzata per il trasporto pubblico (grafo tempo-dipendente).
 * L'obiettivo primario è trovare il percorso che minimizza l'orario di arrivo.
 */
public class PathFinding {

    /**
     * Classe interna per rappresentare lo stato di un nodo (fermata) durante la ricerca.
     * Contiene la fermata e l'orario di arrivo, ed è comparabile per essere usata
     * in una PriorityQueue.
     */
    private static class Stato implements Comparable<Stato> {
        final Fermata fermata;
        final LocalTime orarioArrivo;

        Stato(Fermata fermata, LocalTime orarioArrivo) {
            this.fermata = fermata;
            this.orarioArrivo = orarioArrivo;
        }

        @Override
        public int compareTo(Stato other) {
            return this.orarioArrivo.compareTo(other.orarioArrivo);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Stato stato = (Stato) o;
            return Objects.equals(fermata, stato.fermata) && Objects.equals(orarioArrivo, stato.orarioArrivo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fermata, orarioArrivo);
        }
    }

    /**
     * Trova il percorso ottimale (arrivo più veloce) tra due fermate.
     *
     * @param partenza       La fermata di partenza.
     * @param destinazione   La fermata di destinazione.
     * @param orarioPartenza L'orario in cui si desidera partire.
     * @param giorno         Il giorno della settimana del viaggio.
     * @param tutteLeTratte  La lista completa di tutte le tratte disponibili nel sistema.
     * @return Un oggetto Percorso contenente la soluzione, o null se non viene trovato alcun percorso.
     */
    public Percorso trovaPercorso(Fermata partenza, Fermata destinazione, LocalTime orarioPartenza, DayOfWeek giorno, List<Tratta> tutteLeTratte) {

        // Mappa per memorizzare l'orario di arrivo più basso per ogni fermata
        Map<Fermata, LocalTime> orariArrivoMigliori = new HashMap<>();
        // Mappa per ricostruire il percorso, associando a ogni fermata la "tappa" precedente che l'ha raggiunta
        Map<Fermata, Percorso.Tappa> predecessori = new HashMap<>();
        // Coda di priorità per gestire le fermate da visitare, ordinate per orario di arrivo
        PriorityQueue<Stato> pq = new PriorityQueue<>();

        // Inizializzazione: si parte dalla fermata di partenza all'orario specificato
        orariArrivoMigliori.put(partenza, orarioPartenza);
        pq.add(new Stato(partenza, orarioPartenza));

        while (!pq.isEmpty()) {
            Stato statoCorrente = pq.poll();
            Fermata fermataCorrente = statoCorrente.fermata;
            LocalTime orarioArrivoCorrente = statoCorrente.orarioArrivo;

            // Se abbiamo trovato un percorso più veloce per questa fermata, ignoriamo questo stato
            if (orarioArrivoCorrente.isAfter(orariArrivoMigliori.getOrDefault(fermataCorrente, LocalTime.MAX))) {
                continue;
            }

            // Se abbiamo raggiunto la destinazione, possiamo fermarci e ricostruire il percorso
            if (fermataCorrente.equals(destinazione)) {
                return ricostruisciPercorso(destinazione, predecessori);
            }

            // --- FASE DI "RELAXATION": Esplorazione delle tratte e delle fermate successive ---

            // Itera su tutte le tratte per vedere quali servono la fermata corrente
            for (Tratta tratta : tutteLeTratte) {
                List<FermataTratta> fermateDellaTratta = tratta.getFermataTrattaList();
                if (fermateDellaTratta == null || fermateDellaTratta.isEmpty()) continue;

                // Trova l'indice della fermata corrente sulla tratta
                int indiceFermataCorrente = -1;
                for (int i = 0; i < fermateDellaTratta.size(); i++) {
                    if (fermateDellaTratta.get(i).getFermata().equals(fermataCorrente)) {
                        indiceFermataCorrente = i;
                        break;
                    }
                }

                // Se la fermata è su questa tratta, esplora le corse disponibili
                if (indiceFermataCorrente != -1) {
                    Fermata primaFermataTratta = fermateDellaTratta.get(0).getFermata();

                    // Controlla ogni corsa (UnicaTratta) per questa linea
                    for (UnicaTratta corsa : tratta.getUnicaTrattaList()) {
                        OrarioTratta orarioCorsa = corsa.getOrarioTratta();

                        // Verifica se la corsa è attiva nel giorno richiesto
                        if (orarioCorsa.isAttivo() && orarioCorsa.getGiorniSettimana().contains(giorno)) {

                            // --- INIZIO DELLA MODIFICA ---
                            // Calcola l'orario di partenza di questa corsa dalla fermata corrente
                            long minutiDaCapolinea = 0;
                            // Se la fermata corrente non è il capolinea, calcola la distanza.
                            // Altrimenti, la distanza è 0.
                            if (!fermataCorrente.equals(primaFermataTratta)) {
                                minutiDaCapolinea = tratta.getDistanceForTwoFermate(primaFermataTratta, fermataCorrente);
                            }
                            // --- FINE DELLA MODIFICA ---

                            LocalTime partenzaProgrammata = orarioCorsa.getOraInizio().plusMinutes(minutiDaCapolinea);

                            // Se la partenza programmata è dopo il nostro arrivo alla fermata, possiamo prendere questo mezzo
                            if (!partenzaProgrammata.isBefore(orarioArrivoCorrente)) {

                                // Ora calcoliamo l'arrivo a tutte le fermate successive su questa stessa corsa
                                for (int i = indiceFermataCorrente + 1; i < fermateDellaTratta.size(); i++) {
                                    Fermata fermataSuccessiva = fermateDellaTratta.get(i).getFermata();
                                    long tempoPercorrenza = tratta.getDistanceForTwoFermate(fermataCorrente, fermataSuccessiva);
                                    LocalTime nuovoOrarioArrivo = partenzaProgrammata.plusMinutes(tempoPercorrenza);

                                    // Se questo nuovo percorso è migliore di uno trovato in precedenza...
                                    if (nuovoOrarioArrivo.isBefore(orariArrivoMigliori.getOrDefault(fermataSuccessiva, LocalTime.MAX))) {
                                        // ...aggiorniamo i nostri dati
                                        orariArrivoMigliori.put(fermataSuccessiva, nuovoOrarioArrivo);
                                        Percorso.Tappa tappa = new Percorso.Tappa(tratta, fermataCorrente, fermataSuccessiva, partenzaProgrammata, nuovoOrarioArrivo);
                                        predecessori.put(fermataSuccessiva, tappa);
                                        pq.add(new Stato(fermataSuccessiva, nuovoOrarioArrivo));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Se la coda è vuota e non abbiamo raggiunto la destinazione, non esiste un percorso
        return null;
    }

    /**
     * Ricostruisce il percorso finale partendo dalla destinazione e tornando indietro
     * tramite la mappa dei predecessori.
     * @param destinazione La fermata finale del percorso.
     * @param predecessori La mappa che collega ogni fermata alla tappa usata per raggiungerla.
     * @return Un oggetto Percorso completo.
     */
    private Percorso ricostruisciPercorso(Fermata destinazione, Map<Fermata, Percorso.Tappa> predecessori) {
        LinkedList<Percorso.Tappa> tappeInOrdine = new LinkedList<>();
        Fermata fermataAttuale = destinazione;

        // Risale il percorso all'indietro dalla destinazione alla partenza
        // finché non troviamo una fermata che non ha un predecessore (la partenza)
        while (predecessori.containsKey(fermataAttuale)) {
            Percorso.Tappa tappaPrecedente = predecessori.get(fermataAttuale);
            tappeInOrdine.addFirst(tappaPrecedente); // Aggiunge in testa per mantenere l'ordine corretto
            fermataAttuale = tappaPrecedente.getFermataPartenza();
        }

        if (tappeInOrdine.isEmpty()) {
            // Questo caso si verifica se partenza e destinazione coincidono e non è stato fatto nessun viaggio.
            // A seconda dei requisiti, si potrebbe restituire un percorso vuoto o null.
            // Restituire null indica che non è stato trovato un percorso valido.
            return null;
        }

        return new Percorso(tappeInOrdine);
    }
}