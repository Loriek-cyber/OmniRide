package model.pathfinding;

import model.sdata.Fermata;

import java.util.List;

import model.sdata.Tratta;

import java.sql.SQLException;
import java.time.LocalTime;
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
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.priority));
        Map<Fermata, Double> gScore = new HashMap<>();
        Map<Fermata, Fermata> cameFrom = new HashMap<>();

        gScore.put(start, 0.0);
        openSet.add(new Node(start, 0.0));

        while (!openSet.isEmpty()) {
            Fermata current = openSet.poll().fermata;
            if (current.equals(end)) {
                return ricostruisciPercorso(cameFrom, current);
            }

            for (GrafoTrasporti.Collegamento neighbor : grafo.getCollegamenti(current)) {
                Fermata neighborNode = neighbor.getDestinazione();
                double tentativeGScore = gScore.get(current) + neighbor.getTempoPercorrenza();

                if (tentativeGScore < gScore.getOrDefault(neighborNode, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighborNode, current);
                    gScore.put(neighborNode, tentativeGScore);
                    double fScore = tentativeGScore + grafo.calcolaDistanzaEuristica(neighborNode, end);
                    openSet.add(new Node(neighborNode, fScore));
                }
            }
        }
        return null; // Nessun percorso trovato
    }

    private static Percorso ricostruisciPercorso(Map<Fermata, Fermata> cameFrom, Fermata current) {
        List<SegmentoPercorso> segmenti = new LinkedList<>();
        double costoTotale = 0.0;

        Fermata prec = current;
        while (cameFrom.containsKey(current)) {
            prec = cameFrom.get(current);
            // Ricostruisci il segmento, editare per includere tempi e costi accurati
            SegmentoPercorso segmento = new SegmentoPercorso();
            segmento.setFermataIn(prec);
            segmento.setFermataOu(current);
            segmenti.add(0, segmento);
            current = prec;
        }

        return new Percorso(segmenti, costoTotale);
    }

    private static class Node {
        Fermata fermata;
        double priority;

        Node(Fermata fermata, double priority) {
            this.fermata = fermata;
            this.priority = priority;
        }
    }
}
