package model.sdata;

import java.io.Serializable;

public class FermataTratta implements Serializable {
    private Long id; // PK della tabella Fermata_Tratta
    private Long idTratta;
    private Fermata fermata;
    private int sequenza; // Ordine della fermata nella tratta
    private int tempoProssimaFermata; // in minuti

    // Questo campo può essere popolato dopo il recupero per comodità
    private transient Fermata prossimaFermata;

    public FermataTratta() {}

    // Costruttore per la creazione di nuove istanze (l'ID verrà generato dal DB)
    public FermataTratta(Long idTratta, Fermata fermata, int sequenza, int tempoProssimaFermata) {
        this.idTratta = idTratta;
        this.fermata = fermata;
        this.sequenza = sequenza;
        this.tempoProssimaFermata = tempoProssimaFermata;
    }

    //costruttore singolo

    public FermataTratta(Long idTratta, Fermata fermata,int tempoProssimaFermata) {
        this.idTratta = idTratta;
        this.fermata = fermata;
        this.tempoProssimaFermata = tempoProssimaFermata;
    }

    // Getters e Setters per tutti i campi
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdTratta() { return idTratta; }
    public void setIdTratta(Long idTratta) { this.idTratta = idTratta; }
    public Fermata getFermata() { return fermata; }
    public void setFermata(Fermata fermata) { this.fermata = fermata; }
    public int getSequenza() { return sequenza; }
    public void setSequenza(int sequenza) { this.sequenza = sequenza; }
    public int getTempoProssimaFermata() { return tempoProssimaFermata; }
    public void setTempoProssimaFermata(int tempoProssimaFermata) { this.tempoProssimaFermata = tempoProssimaFermata; }
    public Fermata getProssimaFermata() { return prossimaFermata; }
    public void setProssimaFermata(Fermata prossimaFermata) { this.prossimaFermata = prossimaFermata; }
}