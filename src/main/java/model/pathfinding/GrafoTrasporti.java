package model.pathfinding;

import model.sdata.*;
import java.util.*;

/**
 * Rappresenta il grafo delle fermate e dei collegamenti per il pathfinding
 */
public class GrafoTrasporti {
    
    // Mappa principale: fermata -> lista di collegamenti possibili
    private Map<Fermata, List<Collegamento>> grafo;
    
    // Cache per le fermate per evitare duplicati
    private Map<Long, Fermata> fermate;
    
    /**
     * Rappresenta un collegamento tra due fermate
     */
    public static class Collegamento {
        private Fermata destinazione;
        private Tratta tratta;
        private int tempoPercorrenza; // in minuti
        private double costo;
        private FermataTratta fermataOrigine;
        private FermataTratta fermataDestinazione;
        
        public Collegamento(Fermata destinazione, Tratta tratta, int tempoPercorrenza, double costo) {
            this.destinazione = destinazione;
            this.tratta = tratta;
            this.tempoPercorrenza = tempoPercorrenza;
            this.costo = costo;
        }
        
        public Collegamento(Fermata destinazione, Tratta tratta, int tempoPercorrenza, double costo, 
                           FermataTratta fermataOrigine, FermataTratta fermataDestinazione) {
            this.destinazione = destinazione;
            this.tratta = tratta;
            this.tempoPercorrenza = tempoPercorrenza;
            this.costo = costo;
            this.fermataOrigine = fermataOrigine;
            this.fermataDestinazione = fermataDestinazione;
        }
        
        // Getters
        public Fermata getDestinazione() { return destinazione; }
        public Tratta getTratta() { return tratta; }
        public int getTempoPercorrenza() { return tempoPercorrenza; }
        public double getCosto() { return costo; }
        public FermataTratta getFermataOrigine() { return fermataOrigine; }
        public FermataTratta getFermataDestinazione() { return fermataDestinazione; }
    }
    
    public GrafoTrasporti() {
        this.grafo = new HashMap<>();
        this.fermate = new HashMap<>();
    }
    
    /**
     * Costruisce il grafo a partire da una lista di tratte
     */
    public void costruisciGrafo(List<Tratta> tratte) {
        for (Tratta tratta : tratte) {
            if (!tratta.isAttiva()) continue;
            
            List<FermataTratta> percorso = tratta.getFermataTrattaList();
            
            // Per ogni fermata nella tratta
            for (int i = 0; i < percorso.size() - 1; i++) {
                FermataTratta fermataCorrente = percorso.get(i);
                FermataTratta fermataProssima = percorso.get(i + 1);
                
                Fermata origine = fermataCorrente.getFermata();
                Fermata destinazione = fermataProssima.getFermata();
                
                // Aggiungi le fermate alla cache
                fermate.putIfAbsent(origine.getId(), origine);
                fermate.putIfAbsent(destinazione.getId(), destinazione);
                
                // Calcola il costo per questa tratta parziale
                double costoSegmento = tratta.getCosto() / (percorso.size() - 1);
                
                // Crea il collegamento con informazioni dettagliate
                Collegamento collegamento = new Collegamento(
                    destinazione,
                    tratta,
                    fermataCorrente.getTempoProssimaFermata(),
                    costoSegmento,
                    fermataCorrente,
                    fermataProssima
                );
                
                // Aggiungi il collegamento al grafo
                grafo.computeIfAbsent(origine, k -> new ArrayList<>()).add(collegamento);
            }
        }
    }
    
    /**
     * Ottiene tutti i collegamenti da una fermata
     */
    public List<Collegamento> getCollegamenti(Fermata fermata) {
        return grafo.getOrDefault(fermata, new ArrayList<>());
    }
    
    /**
     * Verifica se una fermata esiste nel grafo
     */
    public boolean contieneFermata(Fermata fermata) {
        return grafo.containsKey(fermata);
    }
    
    /**
     * Ottiene tutte le fermate del grafo
     */
    public Set<Fermata> getFermate() {
        return new HashSet<>(grafo.keySet());
    }
    
    /**
     * Ottiene una fermata per ID
     */
    public Fermata getFermataById(Long id) {
        return fermate.get(id);
    }
    
    /**
     * Calcola la distanza euristica tra due fermate (per A*)
     */
    public double calcolaDistanzaEuristica(Fermata origine, Fermata destinazione) {
        if (origine.getCoordinate() == null || destinazione.getCoordinate() == null) {
            return 0; // Se non abbiamo coordinate, non possiamo calcolare l'euristica
        }
        
        // Usa la distanza haversine dalle coordinate
        double distanzaKm = origine.getCoordinate().distanzaDa(destinazione.getCoordinate());
        
        // Stima il tempo assumendo una velocità media di 30 km/h per i mezzi pubblici
        double tempoStimato = (distanzaKm / 30.0) * 60; // in minuti
        
        return tempoStimato;
    }
    
    /**
     * Stampa informazioni sul grafo (per debug)
     */
    public void stampaInfoGrafo() {
        System.out.println("Grafo Trasporti:");
        System.out.println("Numero di fermate: " + grafo.size());
        
        int numeroCollegamenti = 0;
        for (List<Collegamento> collegamenti : grafo.values()) {
            numeroCollegamenti += collegamenti.size();
        }
        System.out.println("Numero totale di collegamenti: " + numeroCollegamenti);
        
        // Stampa alcune connessioni di esempio
        int count = 0;
        for (Map.Entry<Fermata, List<Collegamento>> entry : grafo.entrySet()) {
            if (count++ >= 5) break; // Mostra solo le prime 5
            
            Fermata origine = entry.getKey();
            System.out.println("\nFermata: " + origine.getNome());
            
            for (Collegamento col : entry.getValue()) {
                System.out.println("  -> " + col.getDestinazione().getNome() + 
                    " (Tempo: " + col.getTempoPercorrenza() + " min, " +
                    "Costo: €" + String.format("%.2f", col.getCosto()) + ", " +
                    "Tratta: " + col.getTratta().getNome() + ")");
            }
        }
    }
}
