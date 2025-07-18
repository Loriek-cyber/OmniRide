package model.pathfinding;
import model.sdata.Tratta;

import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe rappresenta un intero percorso composto da più segmenti.
 */
public class Percorso {
    private List<SegmentoPercorso> Segmenti;
    private double costo;


    public Percorso() {
    }



    public Percorso(List<SegmentoPercorso> segmenti, double costo) {
        Segmenti = segmenti;
        this.costo = costo;
    }
    
    /**
     * Costruttore specifico per trasformare una singola Tratta in un Percorso
     * @param tratta la tratta da trasformare
     * @param fermataPartenza fermata di partenza
     * @param fermataArrivo fermata di arrivo
     */
    public Percorso(model.sdata.Tratta tratta, model.sdata.Fermata fermataPartenza, model.sdata.Fermata fermataArrivo) {
        this.Segmenti = new ArrayList<>();
        
        // Crea un singolo segmento per la tratta
        SegmentoPercorso segmento = new SegmentoPercorso();
        segmento.setId_tratta(tratta.getId());
        segmento.setFermataIn(fermataPartenza);
        segmento.setFermataOu(fermataArrivo);
        
        // Calcola orari di partenza e arrivo
        if (tratta.getOrari() != null && !tratta.getOrari().isEmpty()) {
            model.sdata.OrarioTratta primoOrario = tratta.getOrari().get(0);
            if (primoOrario != null && primoOrario.getOraPartenza() != null) {
                segmento.setTempo_partenza(primoOrario.getOraPartenza().toLocalTime());
                segmento.setTempo_arrivo(primoOrario.getOraPartenza().toLocalTime().plusMinutes(30)); // Stima
            }
        }
        
        this.Segmenti.add(segmento);
        this.costo = tratta.getCosto();
    }
    
    /**
     * Costruttore che crea un Percorso con un unico grande segmento che comprende tutta la tratta
     * @param tratta la tratta completa da trasformare in un unico segmento
     */
    public Percorso(model.sdata.Tratta tratta) {
        this.Segmenti = new ArrayList<>();
        
        if (tratta == null || tratta.getFermataTrattaList() == null || tratta.getFermataTrattaList().isEmpty()) {
            this.costo = 0;
            return;
        }
        
        // Crea un unico segmento che comprende tutta la tratta
        SegmentoPercorso segmento = new SegmentoPercorso();
        segmento.setId_tratta(tratta.getId());
        
        // Prima fermata della tratta
        segmento.setFermataIn(tratta.getFermataTrattaList().get(0).getFermata());
        
        // Ultima fermata della tratta
        segmento.setFermataOu(tratta.getFermataTrattaList().get(tratta.getFermataTrattaList().size() - 1).getFermata());
        
        // Calcola il tempo totale di percorrenza
        int tempoTotale = 0;
        for (int i = 0; i < tratta.getFermataTrattaList().size() - 1; i++) {
            tempoTotale += tratta.getFermataTrattaList().get(i).getTempoProssimaFermata();
        }
        
        // Numero totale di fermate
        segmento.setNumero_fermate(tratta.getFermataTrattaList().size());
        
        // Imposta gli orari
        if (tratta.getOrari() != null && !tratta.getOrari().isEmpty()) {
            model.sdata.OrarioTratta primoOrario = tratta.getOrari().get(0);
            if (primoOrario != null && primoOrario.getOraPartenza() != null) {
                segmento.setTempo_partenza(primoOrario.getOraPartenza().toLocalTime());
                segmento.setTempo_arrivo(primoOrario.getOraPartenza().toLocalTime().plusMinutes(tempoTotale));
            }
        }
        
        this.Segmenti.add(segmento);
        this.costo = tratta.getCosto();
    }

    public List<SegmentoPercorso> getSegmenti() {
        return Segmenti;
    }

    public void setSegmenti(List<SegmentoPercorso> segmenti) {
        Segmenti = segmenti;
    }

    public void addSegmenti(SegmentoPercorso segmenti) {
        if (this.Segmenti == null) {
            this.Segmenti = new ArrayList<>();
            this.Segmenti.add(segmenti);
        }
        else {
            this.Segmenti.add(segmenti);
        }
    }



    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

}
