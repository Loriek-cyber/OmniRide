package model.pathfinding;

import model.sdata.Fermata;
import model.sdata.Tratta;
import model.sdata.FermataTratta;
import model.sdata.OrarioTratta;
import model.pathfinding.GrafoTrasporti.Collegamento;
import model.util.TimeCalculator;

import java.time.LocalTime;
import java.time.Duration;
import java.util.*;

/**
 * Classe principale per l'algoritmo di pathfinding migliorato.
 * Utilizza l'algoritmo A* per trovare il percorso ottimale tra due fermate in termini di tempo di percorrenza totale,
 * che include sia il tempo di viaggio sui mezzi sia i tempi di attesa.
 */
public class Pathfinder {

    /**
     * Metodo di utility per avviare la ricerca del percorso.
     */
    public static Percorso find(Fermata f1, Fermata f2, List<Tratta> tratte, LocalTime start) {
        GrafoTrasporti grafo = new GrafoTrasporti();
        grafo.costruisciGrafo(tratte);
        return calcolaPercorso(grafo, f1, f2, start);
    }

    /**
     * Trova il percorso migliore partendo dall'ora corrente.
     */
    public static Percorso trovaPercorsoMigliore(Fermata fermataPartenza, Fermata fermataArrivo, List<Tratta> tratteAttive) {
        LocalTime currentTime = LocalTime.now();
        return find(fermataPartenza, fermataArrivo, tratteAttive, currentTime);
    }

    /**
     * Implementazione migliorata dell'algoritmo A*.
     */
    private static Percorso calcolaPercorso(GrafoTrasporti grafo, Fermata start, Fermata end, LocalTime currentStartTime) {
        PriorityQueue<TimeNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.priority));

        Map<Fermata, Double> gScore = new HashMap<>();
        Map<Fermata, Fermata> cameFrom = new HashMap<>();
        Map<Fermata, LocalTime> arrivalTimes = new HashMap<>();
        Map<Fermata, LocalTime> departureTimes = new HashMap<>();  // AGGIUNTO: tempi di partenza
        Map<Fermata, Collegamento> connectionTo = new HashMap<>();

        // Inizializzazione per il nodo di partenza
        gScore.put(start, 0.0);
        arrivalTimes.put(start, currentStartTime);
        departureTimes.put(start, currentStartTime);  // AGGIUNTO
        openSet.add(new TimeNode(start, 0.0, currentStartTime));

        while (!openSet.isEmpty()) {
            TimeNode currentNode = openSet.poll();
            Fermata current = currentNode.fermata;
            LocalTime currentTime = currentNode.arrivalTime;

            if (current.equals(end)) {
                return ricostruisciPercorso(cameFrom, current, connectionTo, arrivalTimes, departureTimes);
            }

            // Ottimizzazione: evita di riprocessare nodi con gScore peggiore
            if (gScore.get(current) < currentNode.priority - grafo.calcolaDistanzaEuristica(current, end)) {
                continue;
            }

            // Esplora i vicini
            for (GrafoTrasporti.Collegamento collegamento : grafo.getCollegamenti(current)) {
                Fermata neighborNode = collegamento.getDestinazione();

                // Calcola gli orari di partenza e arrivo per questo specifico segmento
                LocalTime[] orariSegmento = calcolaOrariSegmento(collegamento, currentTime);
                if (orariSegmento == null) {
                    continue; // Se non ci sono corse disponibili, salta questo collegamento
                }

                LocalTime departureTime = orariSegmento[0];
                LocalTime arrivalAtNeighbor = orariSegmento[1];

                // Calcola il tempo di attesa alla fermata
                double waitingTime = calcolaTempoAttesa(currentTime, departureTime);

                // Calcola il tempo di viaggio
                double travelTime = Duration.between(departureTime, arrivalAtNeighbor).toMinutes();
                if (travelTime < 0) {
                    travelTime += 24 * 60; // Gestisce il caso overnight
                }

                // Calcola il gScore "tentativo" per il vicino
                double tentativeGScore = gScore.get(current) + waitingTime + travelTime;

                // Se questo percorso per il vicino è migliore di uno precedente, lo registriamo
                if (tentativeGScore < gScore.getOrDefault(neighborNode, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighborNode, current);
                    gScore.put(neighborNode, tentativeGScore);
                    arrivalTimes.put(neighborNode, arrivalAtNeighbor);
                    // Memorizza il tempo di partenza dal nodo corrente per raggiungere il vicino
                    // Salviamo il tempo di partenza dal nodo corrente nella mappa del nodo vicino
                    departureTimes.put(neighborNode, departureTime);  // Tempo di partenza da current per arrivare a neighborNode
                    connectionTo.put(neighborNode, collegamento);

                    // Calcola l'fScore e aggiunge il vicino all'open set
                    double fScore = tentativeGScore + grafo.calcolaDistanzaEuristica(neighborNode, end);
                    openSet.add(new TimeNode(neighborNode, fScore, arrivalAtNeighbor));
                }
            }
        }
        return null;
    }

    /**
     * Calcola il tempo di attesa tra due orari, gestendo correttamente i casi overnight.
     */
    private static double calcolaTempoAttesa(LocalTime arrivoFermata, LocalTime partenzaMezzo) {
        long waitingMinutes = Duration.between(arrivoFermata, partenzaMezzo).toMinutes();

        // Se il risultato è negativo, significa che la partenza è il giorno dopo
        if (waitingMinutes < 0) {
            waitingMinutes += 24 * 60;
        }

        return waitingMinutes;
    }

    /**
     * Versione migliorata del calcolo degli orari del segmento.
     */
    private static LocalTime[] calcolaOrariSegmento(GrafoTrasporti.Collegamento collegamento, LocalTime arrivoAllaPartenza) {
        Tratta tratta = collegamento.getTratta();
        Fermata fermataPartenzaSegmento = collegamento.getFermataOrigine().getFermata();
        Fermata fermataArrivoSegmento = collegamento.getDestinazione();

        List<FermataTratta> fermateTratta = tratta.getFermataTrattaList();

        // Trova gli indici delle fermate di partenza e arrivo
        int indiceFermataPartenza = -1;
        int indiceFermataArrivo = -1;
        long tempoPerRaggiungereLaPartenza = 0;
        long tempoTotaleSegmento = 0;

        for (int i = 0; i < fermateTratta.size(); i++) {
            FermataTratta ft = fermateTratta.get(i);

            if (ft.getFermata().equals(fermataPartenzaSegmento)) {
                indiceFermataPartenza = i;
            }
            if (ft.getFermata().equals(fermataArrivoSegmento)) {
                indiceFermataArrivo = i;
                break;
            }

            // Accumula il tempo per raggiungere la fermata corrente
            if (indiceFermataPartenza == -1) {
                tempoPerRaggiungereLaPartenza += ft.getTempoProssimaFermata();
            } else if (indiceFermataArrivo == -1) {
                tempoTotaleSegmento += ft.getTempoProssimaFermata();
            }
        }

        if (indiceFermataPartenza == -1 || indiceFermataArrivo == -1) {
            System.err.println("Errore: fermata non trovata nella tratta " + tratta.getId());
            return null;
        }

        // Trova il miglior orario di partenza
        LocalTime migliorOrarioPartenza = null;
        LocalTime migliorOrarioArrivo = null;
        long minAttesa = Long.MAX_VALUE;

        for (OrarioTratta orario : tratta.getOrari()) {
            if (!orario.isAttivo() || orario.getOraPartenza() == null) continue;

            LocalTime partenzaDaCapolinea = orario.getOraPartenza().toLocalTime();
            LocalTime arrivoAllaFermataPartenza = partenzaDaCapolinea.plusMinutes(tempoPerRaggiungereLaPartenza);

            // Verifica se questa corsa è utile (parte dopo il nostro arrivo alla fermata)
            if (!arrivoAllaFermataPartenza.isBefore(arrivoAllaPartenza)) {
                long attesa = Duration.between(arrivoAllaPartenza, arrivoAllaFermataPartenza).toMinutes();
                if (attesa < minAttesa) {
                    minAttesa = attesa;
                    migliorOrarioPartenza = arrivoAllaFermataPartenza;
                    migliorOrarioArrivo = arrivoAllaFermataPartenza.plusMinutes(tempoTotaleSegmento);
                }
            }
        }

        // Se non troviamo una corsa per oggi, prendiamo la prima del giorno dopo
        if (migliorOrarioPartenza == null) {
            LocalTime primoOrarioAssoluto = null;
            for (OrarioTratta orario : tratta.getOrari()) {
                if (!orario.isAttivo() || orario.getOraPartenza() == null) continue;
                LocalTime partenzaCorrente = orario.getOraPartenza().toLocalTime();
                if (primoOrarioAssoluto == null || partenzaCorrente.isBefore(primoOrarioAssoluto)) {
                    primoOrarioAssoluto = partenzaCorrente;
                }
            }
            if (primoOrarioAssoluto != null) {
                migliorOrarioPartenza = primoOrarioAssoluto.plusMinutes(tempoPerRaggiungereLaPartenza);
                migliorOrarioArrivo = migliorOrarioPartenza.plusMinutes(tempoTotaleSegmento);
            }
        }

        if (migliorOrarioPartenza == null) {
            return null; // Nessun orario disponibile
        }

        return new LocalTime[]{migliorOrarioPartenza, migliorOrarioArrivo};
    }

    /**
     * Ricostruisce il percorso finale migliorato.
     */
    private static Percorso ricostruisciPercorso(Map<Fermata, Fermata> cameFrom, Fermata current,
                                                 Map<Fermata, Collegamento> connectionTo,
                                                 Map<Fermata, LocalTime> arrivalTimes,
                                                 Map<Fermata, LocalTime> departureTimes) {
        LinkedList<SegmentoPercorso> segmenti = new LinkedList<>();
        double costoTotale = 0.0;

        Fermata step = current;
        while (cameFrom.containsKey(step)) {
            Fermata previous = cameFrom.get(step);
            Collegamento collegamento = connectionTo.get(step);

            if (collegamento == null) {
                System.err.println("Errore: collegamento nullo durante la ricostruzione del percorso.");
                break;
            }

            SegmentoPercorso segmento = new SegmentoPercorso();
            segmento.setFermataIn(previous);
            segmento.setFermataOu(step);
            segmento.setId_tratta(collegamento.getTratta().getId());

            // Usa gli orari memorizzati durante l'esplorazione
            // Il tempo di partenza è quando si parte dal nodo precedente (stored in departureTimes for step)
            // Il tempo di arrivo è quando si arriva al nodo corrente
            LocalTime tempoPartenza = departureTimes.get(step);  // Questo è quando si parte da previous per andare a step
            LocalTime tempoArrivo = arrivalTimes.get(step);      // Questo è quando si arriva a step

            // Verifica di coerenza degli orari
            if (tempoPartenza != null && tempoArrivo != null) {
                segmento.setTempo_partenza(tempoPartenza);
                segmento.setTempo_arrivo(tempoArrivo);
            } else {
                System.err.println("Errore: orari mancanti per il segmento " + previous.getId() + " -> " + step.getId());
                // Fallback: ricalcola gli orari
                LocalTime tempoArrivoAlPrecedente = arrivalTimes.get(previous);
                if (tempoArrivoAlPrecedente != null) {
                    LocalTime[] orariSegmento = calcolaOrariSegmento(collegamento, tempoArrivoAlPrecedente);
                    if (orariSegmento != null) {
                        segmento.setTempo_partenza(orariSegmento[0]);
                        segmento.setTempo_arrivo(orariSegmento[1]);
                    }
                }
            }

            costoTotale += collegamento.getCosto();

            int numeroFermate = calcolaNumeroFermate(collegamento.getTratta(), previous, step);
            segmento.setNumero_fermate(numeroFermate);

            segmenti.addFirst(segmento);
            step = previous;
        }

        return new Percorso(segmenti, costoTotale);
    }

    /**
     * Calcola il numero di fermate tra due punti su una stessa tratta.
     */
    private static int calcolaNumeroFermate(Tratta tratta, Fermata fermataPartenza, Fermata fermataArrivo) {
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        int indexPartenza = -1;
        int indexArrivo = -1;

        for (int i = 0; i < fermate.size(); i++) {
            FermataTratta ft = fermate.get(i);
            if (ft.getFermata().equals(fermataPartenza)) {
                indexPartenza = i;
            }
            if (ft.getFermata().equals(fermataArrivo)) {
                indexArrivo = i;
            }
        }

        if (indexPartenza != -1 && indexArrivo != -1 && indexPartenza < indexArrivo) {
            return indexArrivo - indexPartenza;
        }

        return 1; // Fallback
    }

    /**
     * Classe interna per rappresentare un nodo nella coda di priorità dell'algoritmo A*.
     */
    private static class TimeNode {
        Fermata fermata;
        double priority; // Corrisponde a fScore
        LocalTime arrivalTime;

        TimeNode(Fermata fermata, double priority, LocalTime arrivalTime) {
            this.fermata = fermata;
            this.priority = priority;
            this.arrivalTime = arrivalTime;
        }
    }
}