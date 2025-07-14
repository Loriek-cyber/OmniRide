package model.pathfinding;
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
