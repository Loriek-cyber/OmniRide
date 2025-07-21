package model.pathfinding;

import model.sdata.*;
import java.util.*;

/**
 * Rappresenta il grafo delle fermate e dei collegamenti per il pathfinding.
 * Questa classe costruisce una struttura dati a grafo a partire da un elenco di tratte,
 * dove i nodi sono le fermate e gli archi rappresentano i collegamenti diretti tra di esse.
 */
public class GrafoTrasporti {
    
    // Mappa principale che definisce il grafo. La chiave è una fermata di origine
    // e il valore è una lista di collegamenti in partenza da quella fermata.
    private Map<Fermata, List<Collegamento>> grafo;
    
    // Cache per memorizzare le fermate per ID, per evitare duplicati e recuperi rapidi.
    private Map<Long, Fermata> fermate;
    
    /**
     * Rappresenta un collegamento (un arco nel grafo) tra due fermate.
     * Contiene informazioni sulla destinazione, la tratta, il tempo di percorrenza e il costo.
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
        
        // Metodi getter per accedere ai dati del collegamento.
        public Fermata getDestinazione() { return destinazione; }
        public Tratta getTratta() { return tratta; }
        public int getTempoPercorrenza() { return tempoPercorrenza; }
        public double getCosto() { return costo; }
        public FermataTratta getFermataOrigine() { return fermataOrigine; }
        public FermataTratta getFermataDestinazione() { return fermataDestinazione; }
    }
    
    /**
     * Costruttore di GrafoTrasporti. Inizializza le strutture dati del grafo.
     */
    public GrafoTrasporti() {
        this.grafo = new HashMap<>();
        this.fermate = new HashMap<>();
    }
    
    /**
     * Costruisce il grafo a partire da una lista di tratte.
     * Itera su ogni tratta e, per ogni coppia di fermate consecutive, crea un collegamento (arco) nel grafo.
     * @param tratte La lista di tutte le tratte disponibili.
     */
    public void costruisciGrafo(List<Tratta> tratte) {
        for (Tratta tratta : tratte) {
            // Ignora le tratte non attive.
            if (!tratta.isAttiva()) continue;
            
            List<FermataTratta> percorso = tratta.getFermataTrattaList();
            
            // Itera su tutte le fermate della tratta per creare i collegamenti.
            for (int i = 0; i < percorso.size() - 1; i++) {
                FermataTratta fermataCorrente = percorso.get(i);
                FermataTratta fermataProssima = percorso.get(i + 1);
                
                Fermata origine = fermataCorrente.getFermata();
                Fermata destinazione = fermataProssima.getFermata();
                
                // Aggiunge le fermate alla cache se non sono già presenti.
                fermate.putIfAbsent(origine.getId(), origine);
                fermate.putIfAbsent(destinazione.getId(), destinazione);
                
                // Calcola il costo per questo segmento di tratta.
                // NOTA: Questo è un calcolo approssimativo che distribuisce il costo totale della tratta
                // in modo uniforme tra le fermate. Potrebbe non essere accurato se il costo non è lineare.
                double costoSegmento = tratta.getCosto() / (percorso.size() - 1);
                
                // Crea un nuovo oggetto Collegamento con tutte le informazioni necessarie.
                Collegamento collegamento = new Collegamento(
                    destinazione,
                    tratta,
                    fermataCorrente.getTempoProssimaFermata(),
                    costoSegmento,
                    fermataCorrente,
                    fermataProssima
                );
                
                // Aggiunge il collegamento al grafo. Se la fermata di origine non è ancora nel grafo,
                // viene creata una nuova lista di collegamenti.
                grafo.computeIfAbsent(origine, k -> new ArrayList<>()).add(collegamento);
            }
        }
    }
    
    /**
     * Restituisce tutti i collegamenti in uscita da una data fermata.
     * @param fermata La fermata di origine.
     * @return Una lista di collegamenti; la lista è vuota se non ci sono collegamenti in uscita.
     */
    public List<Collegamento> getCollegamenti(Fermata fermata) {
        return grafo.getOrDefault(fermata, new ArrayList<>());
    }
    
    /**
     * Verifica se una fermata è presente nel grafo.
     * @param fermata La fermata da controllare.
     * @return true se la fermata esiste come chiave nel grafo, false altrimenti.
     */
    public boolean contieneFermata(Fermata fermata) {
        return grafo.containsKey(fermata);
    }
    
    /**
     * Restituisce l'insieme di tutte le fermate (nodi) presenti nel grafo.
     * @return Un Set di oggetti Fermata.
     */
    public Set<Fermata> getFermate() {
        return new HashSet<>(grafo.keySet());
    }
    
    /**
     * Recupera un oggetto Fermata dalla cache tramite il suo ID.
     * @param id L'ID della fermata.
     * @return L'oggetto Fermata corrispondente, o null se non trovato.
     */
    public Fermata getFermataById(Long id) {
        return fermate.get(id);
    }
    
    /**
     * Calcola una distanza euristica tra due fermate.
     * Questa funzione è usata dall'algoritmo A* per stimare il "costo" (in questo caso, il tempo)
     * per raggiungere la destinazione da una data fermata.
     * @param origine La fermata di partenza.
     * @param destinazione La fermata di arrivo.
     * @return Il tempo stimato in minuti per coprire la distanza.
     */
    public double calcolaDistanzaEuristica(Fermata origine, Fermata destinazione) {
        // Se le coordinate non sono disponibili, non è possibile calcolare un'euristica significativa.
        if (origine.getCoordinate() == null || destinazione.getCoordinate() == null) {
            return 0;
        }
        
        // Calcola la distanza in linea d'aria (Haversine) tra le due fermate.
        double distanzaKm = origine.getCoordinate().distanzaDa(destinazione.getCoordinate());
        
        // Stima il tempo di percorrenza basandosi su una velocità media ipotetica dei mezzi pubblici.
        // Questo valore (30 km/h) è un'approssimazione e potrebbe essere perfezionato.
        double tempoStimato = (distanzaKm / 30.0) * 60; // in minuti
        
        return tempoStimato;
    }
    
    /**
     * Stampa informazioni di base sul grafo, utile per il debug.
     * Mostra il numero totale di fermate e collegamenti, e un esempio di alcune connessioni.
     */
    public void stampaInfoGrafo() {
        System.out.println("Grafo Trasporti:");
        System.out.println("Numero di fermate: " + grafo.size());
        
        int numeroCollegamenti = 0;
        for (List<Collegamento> collegamenti : grafo.values()) {
            numeroCollegamenti += collegamenti.size();
        }
        System.out.println("Numero totale di collegamenti: " + numeroCollegamenti);
        
        // Stampa i dettagli per un piccolo sottoinsieme di fermate per un'ispezione rapida.
        int count = 0;
        for (Map.Entry<Fermata, List<Collegamento>> entry : grafo.entrySet()) {
            if (count++ >= 5) break; // Limita l'output per non inondare la console.
            Fermata origine = entry.getKey();
            System.out.println(" Fermata: " + origine.getNome());
            
            for (Collegamento col : entry.getValue()) {
                System.out.println("  -> " + col.getDestinazione().getNome() + 
                    " (Tempo: " + col.getTempoPercorrenza() + " min, " +
                    "Costo: €" + String.format("%.2f", col.getCosto()) + ", " +
                    "Tratta: " + col.getTratta().getNome() + ")");
            }
        }
    }
}