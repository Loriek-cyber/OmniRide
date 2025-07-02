package model.sdata;
import java.util.List;

public class Risultati {
    private List<Percorso> percorsi;
    private int durataTotale; //minuti
    private int numeroCambi;


    public Risultati(List<Percorso> percorsi, int durataTotale) {
        this.percorsi = percorsi;
        this.durataTotale = durataTotale;
        this.numeroCambi = percorsi.size();
    }

    public List<Percorso> getPercorsi() {
        return percorsi;
    }

    public void setPercorsi(List<Percorso> percorsi) {
        this.percorsi = percorsi;
    }

    public int getDurataTotale() {
        return durataTotale;
    }

    public void setDurataTotale(int durataTotale) {
        this.durataTotale = durataTotale;
    }
}
