package model.pathfinding;

import model.sdata.Fermata;
import model.sdata.Tratta;
import model.sdata.FermataTratta;
import model.sdata.OrarioTratta;
import model.pathfinding.GrafoTrasporti.Collegamento;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.Duration;
import java.util.*;

public class Pathfinder {
    public static Percorso find(Fermata f1,Fermata f2, List<Tratta> tratte, LocalTime start) {
        GrafoTrasporti grafo = new GrafoTrasporti();
        grafo.costruisciGrafo(tratte);
        // Usa un algoritmo di pathfinding, ad esempio A*, edita secondo le necessità
        // Questo è un placeholder per l'implementazione del pathfinding
        // Da sostituire con algoritmo completo

        return calcolaPercorso(grafo, f1, f2, start);
    }

    public static Percorso trovaPercorsoMigliore(Fermata fermataPartenza, Fermata fermataArrivo, List<Tratta> tratteAttive, List<Fermata> tutteLeFermate) {
        LocalTime currentTime = LocalTime.now(); // Usa l'ora corrente
        return find(fermataPartenza, fermataArrivo, tratteAttive, currentTime);
    }

    private static Percorso calcolaPercorso(GrafoTrasporti grafo, Fermata start, Fermata end, LocalTime currentStartTime) {
        PriorityQueue<TimeNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.priority));
        Map<Fermata, Double> gScore = new HashMap<>();
        Map<Fermata, Fermata> cameFrom = new HashMap<>();
        Map<Fermata, LocalTime> arrivalTimes = new HashMap<>();

        gScore.put(start, 0.0);
        arrivalTimes.put(start, currentStartTime);
        openSet.add(new TimeNode(start, 0.0, currentStartTime));

        while (!openSet.isEmpty()) {
            TimeNode currentNode = openSet.poll();
            Fermata current = currentNode.fermata;
            LocalTime currentTime = currentNode.arrivalTime;
            
            if (current.equals(end)) {
                return ricostruisciPercorso(cameFrom, current, grafo, arrivalTimes);
            }

            for (GrafoTrasporti.Collegamento neighbor : grafo.getCollegamenti(current)) {
                Fermata neighborNode = neighbor.getDestinazione();
                
                // Calcola il prossimo orario utile per questa tratta
                LocalTime nextDepartureTime = calcolaProssimoOrario(neighbor.getTratta(), currentTime);
                if (nextDepartureTime == null) continue; // Non ci sono orari validi
                
                // Calcola il tempo di attesa + tempo di percorrenza
                double waitingTime = Duration.between(currentTime, nextDepartureTime).toMinutes();
                if (waitingTime < 0) waitingTime += 24 * 60; // Gestisce il cambio di giorno
                
                double tentativeGScore = gScore.get(current) + waitingTime + neighbor.getTempoPercorrenza();
                LocalTime neighborArrivalTime = nextDepartureTime.plusMinutes(neighbor.getTempoPercorrenza());

                if (tentativeGScore < gScore.getOrDefault(neighborNode, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighborNode, current);
                    gScore.put(neighborNode, tentativeGScore);
                    arrivalTimes.put(neighborNode, neighborArrivalTime);
                    double fScore = tentativeGScore + grafo.calcolaDistanzaEuristica(neighborNode, end);
                    openSet.add(new TimeNode(neighborNode, fScore, neighborArrivalTime));
                }
            }
        }
        return null; // Nessun percorso trovato
    }

    private static Percorso ricostruisciPercorso(Map<Fermata, Fermata> cameFrom, Fermata current, GrafoTrasporti grafo, Map<Fermata, LocalTime> arrivalTimes) {
        List<SegmentoPercorso> segmenti = new LinkedList<>();
        double costoTotale = 0.0;

        Fermata prec = current;
        while (cameFrom.containsKey(current)) {
            prec = cameFrom.get(current);
            SegmentoPercorso segmento = new SegmentoPercorso();
            segmento.setFermataIn(prec);
            segmento.setFermataOu(current);
            
            final Fermata finalCurrent = current;
            Optional<GrafoTrasporti.Collegamento> collegamentoOpt = grafo.getCollegamenti(prec).stream()
                .filter(c -> c.getDestinazione().equals(finalCurrent))
                .findFirst();
            
            if (collegamentoOpt.isPresent()) {
                GrafoTrasporti.Collegamento collegamento = collegamentoOpt.get();
                segmento.setId_tratta(collegamento.getTratta().getId());
                
                // IMPORTANTE: NON ricalcolare gli orari, usa quelli già calcolati dal pathfinding!
                LocalTime tempoArrivoSegmento = arrivalTimes.get(current);
                LocalTime tempoArrivoAllaFermataPrec = arrivalTimes.get(prec);
                
                // Il tempo di partenza è l'arrivo alla fermata precedente più eventuale attesa
                // Ma questo dovrebbe essere già stato calcolato nel pathfinding
                LocalTime tempoPartenzaSegmento = tempoArrivoSegmento.minusMinutes(collegamento.getTempoPercorrenza());
                
                // Se c'è discrepanza, usa il tempo di arrivo alla fermata precedente
                if (tempoArrivoAllaFermataPrec != null && tempoPartenzaSegmento.isBefore(tempoArrivoAllaFermataPrec)) {
                    tempoPartenzaSegmento = tempoArrivoAllaFermataPrec;
                }
                
                // Fallback se non abbiamo i tempi dal pathfinding
                if (tempoPartenzaSegmento == null || tempoArrivoSegmento == null) {
                    LocalTime[] tempiCalcolati = calcolaTempiSegmento(collegamento.getTratta(), prec, current, 
                        tempoPartenzaSegmento != null ? tempoPartenzaSegmento : LocalTime.now());
                    if (tempiCalcolati != null) {
                        tempoPartenzaSegmento = tempiCalcolati[0];
                        tempoArrivoSegmento = tempiCalcolati[1];
                    } else {
                        // Fallback finale
                        List<OrarioTratta> orari = collegamento.getTratta().getOrari();
                        if (!orari.isEmpty()) {
                            OrarioTratta orario = orari.get(0);
                            LocalTime baseTime = orario.getOraPartenza().toLocalTime();
                            tempoPartenzaSegmento = baseTime;
                            tempoArrivoSegmento = baseTime.plusMinutes(collegamento.getTempoPercorrenza());
                        }
                    }
                }
                
                segmento.setTempo_partenza(tempoPartenzaSegmento);
                segmento.setTempo_arrivo(tempoArrivoSegmento);
                
                costoTotale += collegamento.getCosto();
                
                // Calcola il numero di fermate tra le due fermate
                int numeroFermate = calcolaNumeroFermate(collegamento.getTratta(), prec, current);
                segmento.setNumero_fermate(numeroFermate);
            }
            segmenti.add(0, segmento);
            current = prec;
        }

        return new Percorso(segmenti, costoTotale);
    }

    /**
     * Calcola il numero di fermate tra due fermate in una tratta
     */
    private static int calcolaNumeroFermate(Tratta tratta, Fermata fermataPartenza, Fermata fermataArrivo) {
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        int indexPartenza = -1;
        int indexArrivo = -1;
        
        for (int i = 0; i < fermate.size(); i++) {
            FermataTratta ft = fermate.get(i);
            if (ft.getFermata().getId().equals(fermataPartenza.getId())) {
                indexPartenza = i;
            }
            if (ft.getFermata().getId().equals(fermataArrivo.getId())) {
                indexArrivo = i;
            }
        }
        
        if (indexPartenza != -1 && indexArrivo != -1 && indexPartenza < indexArrivo) {
            return indexArrivo - indexPartenza;
        }
        
        return 1; // Default fallback
    }
    
    /**
     * Calcola il prossimo orario utile per una tratta dato l'orario corrente
     */
    private static LocalTime calcolaProssimoOrario(Tratta tratta, LocalTime currentTime) {
        List<OrarioTratta> orari = tratta.getOrari();
        if (orari == null || orari.isEmpty()) {
            return null;
        }
        
        // Trova il primo orario successivo all'orario corrente
        LocalTime nextTime = null;
        for (OrarioTratta orario : orari) {
            if (orario.isAttivo() && orario.getOraPartenza() != null) {
                LocalTime oraPartenza = orario.getOraPartenza().toLocalTime();
                if (!oraPartenza.isBefore(currentTime)) {
                    if (nextTime == null || oraPartenza.isBefore(nextTime)) {
                        nextTime = oraPartenza;
                    }
                }
            }
        }
        
        // Se non c'è un orario successivo oggi, prendi il primo orario del giorno successivo
        if (nextTime == null) {
            for (OrarioTratta orario : orari) {
                if (orario.isAttivo() && orario.getOraPartenza() != null) {
                    LocalTime oraPartenza = orario.getOraPartenza().toLocalTime();
                    if (nextTime == null || oraPartenza.isBefore(nextTime)) {
                        nextTime = oraPartenza;
                    }
                }
            }
        }
        
        return nextTime;
    }
    
    /**
     * Calcola i tempi di partenza e arrivo per un segmento specifico
     * basandosi sulla sequenza delle fermate nella tratta
     */
    private static LocalTime[] calcolaTempiSegmento(Tratta tratta, Fermata fermataPartenza, 
                                                   Fermata fermataArrivo, LocalTime tempoAttualePartenza) {
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        List<OrarioTratta> orari = tratta.getOrari();
        
        if (fermate == null || orari == null || orari.isEmpty()) {
            return null;
        }
        
        // Trova l'orario migliore per partire
        OrarioTratta orarioMigliore = null;
        for (OrarioTratta orario : orari) {
            if (orario.isAttivo() && orario.getOraPartenza() != null) {
                LocalTime oraPartenza = orario.getOraPartenza().toLocalTime();
                if (oraPartenza.isAfter(tempoAttualePartenza) || oraPartenza.equals(tempoAttualePartenza)) {
                    orarioMigliore = orario;
                    break;
                }
            }
        }
        
        // Se non trovato, prendi il primo orario del giorno successivo
        if (orarioMigliore == null && !orari.isEmpty()) {
            orarioMigliore = orari.get(0);
        }
        
        if (orarioMigliore == null) {
            return null;
        }
        
        // Calcola i tempi basandosi sulla sequenza delle fermate
        LocalTime tempoPartenza = orarioMigliore.getOraPartenza().toLocalTime();
        LocalTime tempoArrivo = tempoPartenza; // Mantieni il riferimento al tempo di partenza per sommare i tempi di percorrenza
        int indexPartenza = -1;
        int indexArrivo = -1;
        
        // Trova gli indici delle fermate
        for (int i = 0; i < fermate.size(); i++) {
            FermataTratta ft = fermate.get(i);
            if (ft.getFermata().getId().equals(fermataPartenza.getId())) {
                indexPartenza = i;
            }
            if (ft.getFermata().getId().equals(fermataArrivo.getId())) {
                indexArrivo = i;
            }
        }
        
        if (indexPartenza == -1 || indexArrivo == -1 || indexPartenza >= indexArrivo) {
            return new LocalTime[]{tempoPartenza, tempoPartenza.plusMinutes(30)}; // Fallback
        }
        
        // Calcola il tempo di arrivo considerando tutti i tempi di percorrenza fino al punto di arrivo
        for (int i = indexPartenza; i < indexArrivo; i++) {
            FermataTratta ft = fermate.get(i);
            tempoArrivo = tempoArrivo.plusMinutes(ft.getTempoProssimaFermata());
        }

        return new LocalTime[]{tempoPartenza, tempoArrivo};
    }
    
    /**
     * Calcola il tempo di partenza per un segmento specifico da una fermata
     */
    private static LocalTime calcolaTempoDiPartenzaPerSegmento(Tratta tratta, Fermata fermataPartenza, LocalTime tempoBase) {
        if (tempoBase == null) {
            return calcolaProssimoOrario(tratta, LocalTime.now());
        }
        
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        if (fermate == null || fermate.isEmpty()) {
            return tempoBase;
        }
        
        // Trova l'indice della fermata di partenza
        int indexPartenza = -1;
        for (int i = 0; i < fermate.size(); i++) {
            FermataTratta ft = fermate.get(i);
            if (ft.getFermata().getId().equals(fermataPartenza.getId())) {
                indexPartenza = i;
                break;
            }
        }
        
        if (indexPartenza == -1) {
            return tempoBase;
        }
        
        // Calcola il prossimo orario utile
        LocalTime prossimoOrario = calcolaProssimoOrario(tratta, tempoBase);
        if (prossimoOrario == null) {
            return tempoBase;
        }
        
        // Calcola quanto tempo ci vuole per arrivare alla fermata di partenza dall'inizio della tratta
        LocalTime tempoArrivoAllaFermata = prossimoOrario;
        for (int i = 0; i < indexPartenza; i++) {
            FermataTratta ft = fermate.get(i);
            tempoArrivoAllaFermata = tempoArrivoAllaFermata.plusMinutes(ft.getTempoProssimaFermata());
        }
        
        return tempoArrivoAllaFermata;
    }

    private static class Node {
        Fermata fermata;
        double priority;

        Node(Fermata fermata, double priority) {
            this.fermata = fermata;
            this.priority = priority;
        }
    }
    
    /**
     * Nodo per il pathfinding che tiene conto del tempo di arrivo
     */
    private static class TimeNode {
        Fermata fermata;
        double priority;
        LocalTime arrivalTime;

        TimeNode(Fermata fermata, double priority, LocalTime arrivalTime) {
            this.fermata = fermata;
            this.priority = priority;
            this.arrivalTime = arrivalTime;
        }
    }
}
