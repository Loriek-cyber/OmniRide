package model.pathfinding;

import model.sdata.Fermata;

import java.util.List;

import model.sdata.Tratta;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;

public class Pathfinder {

    public static Percorso trovaPercorsoMigliore(Fermata partenza, Fermata arrivo,List<Tratta> tutteLeTratte, List<Fermata> tutteLeFermate) throws SQLException {
        Map<Fermata, List<SegmentoPercorso>> grafo = new HashMap<>();
        for (Tratta tratta : tutteLeTratte) {
            for (int i = 0; i < tratta.getFermataTrattaList().size() - 1; i++) {
                Fermata fermataCorrente = tratta.getFermataTrattaList().get(i).getFermata();
                Fermata fermataSuccessiva = tratta.getFermataTrattaList().get(i + 1).getFermata();

                SegmentoPercorso segmento = new SegmentoPercorso();
                segmento.setId_tratta(tratta.getId());
                segmento.setFermataIn(fermataCorrente);
                segmento.setFermataOu(fermataSuccessiva);
                
                // Calcola il tempo di percorrenza e il numero di fermate
                try {
                    long tempoPercorrenza = tratta.getDistanceForTwoFermate(fermataCorrente, fermataSuccessiva);
                    segmento.setTempo_partenza(LocalTime.now()); // Semplificato per ora
                    segmento.setTempo_arrivo(LocalTime.now().plusMinutes(tempoPercorrenza));
                    
                    // Calcola il numero di fermate tra le due fermate
                    int numeroFermate = calcolaNumeroFermate(tratta, fermataCorrente, fermataSuccessiva);
                    segmento.setNumero_fermate(numeroFermate);
                    
                } catch (Exception e) {
                    // Se non riesce a calcolare, usa valori di default
                    segmento.setTempo_partenza(LocalTime.now());
                    segmento.setTempo_arrivo(LocalTime.now().plusMinutes(30));
                    segmento.setNumero_fermate(1);
                }

                grafo.computeIfAbsent(fermataCorrente, k -> new ArrayList<>()).add(segmento);
            }
        }

        // 3. Esegui l'algoritmo di Dijkstra
        Map<Fermata, Double> distanze = new HashMap<>();
        Map<Fermata, SegmentoPercorso> predecessori = new HashMap<>();
        PriorityQueue<Fermata> codaPriorita = new PriorityQueue<>(Comparator.comparing(distanze::get));

        for (Fermata fermata : tutteLeFermate) {
            distanze.put(fermata, Double.MAX_VALUE);
        }
        distanze.put(partenza, 0.0);
        codaPriorita.add(partenza);

        while (!codaPriorita.isEmpty()) {
            Fermata fermataCorrente = codaPriorita.poll();

            if (fermataCorrente.equals(arrivo)) {
                break; // Trovato il percorso più breve
            }

            if (grafo.containsKey(fermataCorrente)) {
                for (SegmentoPercorso segmento : grafo.get(fermataCorrente)) {
                    Fermata fermataVicina = segmento.getFermataOu();
                    double pesoSegmento = 1.0; // Esempio: ogni segmento ha peso 1
                    double nuovaDistanza = distanze.get(fermataCorrente) + pesoSegmento;

                    if (nuovaDistanza < distanze.get(fermataVicina)) {
                        distanze.put(fermataVicina, nuovaDistanza);
                        predecessori.put(fermataVicina, segmento);
                        codaPriorita.add(fermataVicina);
                    }
                }
            }
        }

        // 4. Ricostruisci il percorso
        List<SegmentoPercorso> segmenti = new ArrayList<>();
        Fermata fermataAttuale = arrivo;
        double costoTotale = 0.0;
        
        while (predecessori.containsKey(fermataAttuale)) {
            SegmentoPercorso segmento = predecessori.get(fermataAttuale);
            segmenti.add(0, segmento);
            
            // Calcola il costo per questo segmento
            try {
                Tratta tratta = trovaTrattaById(tutteLeTratte, segmento.getId_tratta());
                if (tratta != null) {
                    double costoSegmento = tratta.ncosto(segmento.getNumero_fermate());
                    costoTotale += costoSegmento;
                }
            } catch (Exception e) {
                // Se non riesce a calcolare, usa un costo di default
                costoTotale += 2.0;
            }
            
            fermataAttuale = segmento.getFermataIn();
        }
        
        // Se non ci sono segmenti, il costo è 0
        if (segmenti.isEmpty()) {
            costoTotale = 0.0;
        }

        return new Percorso(segmenti, costoTotale);
    }

    /**
     * Calcola il numero di fermate tra due fermate su una tratta
     */
    private static int calcolaNumeroFermate(Tratta tratta, Fermata fermataInizio, Fermata fermataFine) {
        List<model.sdata.FermataTratta> fermate = tratta.getFermataTrattaList();
        int indiceInizio = -1;
        int indiceFine = -1;
        
        for (int i = 0; i < fermate.size(); i++) {
            if (fermate.get(i).getFermata().equals(fermataInizio)) {
                indiceInizio = i;
            }
            if (fermate.get(i).getFermata().equals(fermataFine)) {
                indiceFine = i;
            }
        }
        
        if (indiceInizio != -1 && indiceFine != -1 && indiceFine > indiceInizio) {
            return indiceFine - indiceInizio;
        }
        
        return 1; // Default
    }
    
    /**
     * Trova una tratta per ID
     */
    private static Tratta trovaTrattaById(List<Tratta> tratte, Long id) {
        for (Tratta tratta : tratte) {
            if (tratta.getId().equals(id)) {
                return tratta;
            }
        }
        return null;
    }
    
    private void init(){
    }
}
