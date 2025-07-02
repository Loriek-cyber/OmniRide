package model.sdata;
//classe intermedi per l'apartenenza a una fermata
public class FermataTratta {
    private Long idTratta;
    private Fermata fermata;
    private Fermata prossimaFermata;
    private int tempoProssimaFermata; //in minuti
    public FermataTratta() {}

    public FermataTratta(Long idTratta, Fermata fermata, Fermata prossimaFermata, int tempoProssimaFermata) {
        this.idTratta = idTratta;
        this.fermata = fermata;
        this.prossimaFermata = prossimaFermata;
        this.tempoProssimaFermata = tempoProssimaFermata;
    }


    public Long getIdTratta() {return idTratta;}
    public void setIdTratta(Long idTratta) {this.idTratta = idTratta;}
    public Fermata getFermata() {return fermata;}
    public void setFermata(Fermata fermata) {this.fermata = fermata;}
    public Fermata getProssimaFermata() {return prossimaFermata;}
    public void setProssimaFermata(Fermata prossimaFermata) {this.prossimaFermata = prossimaFermata;}
    public int getTempoProssimaFermata() {return tempoProssimaFermata;}
    public void setTempoProssimaFermata(int tempoProssimaFermata) {this.tempoProssimaFermata = tempoProssimaFermata;}


    @Override
    public String toString() {
        // String.format è un modo pulito e sicuro per costruire stringhe.
        // Gestisce automaticamente gli oggetti null senza causare errori.
        return String.format(
                "FermataTratta{idTratta=%d, fermata=%s, prossimaFermata=%s, tempoProssimaFermata=%d}",
                idTratta,
                fermata != null ? fermata.toString() : "null",
                prossimaFermata != null ? prossimaFermata.toString() : "null",
                tempoProssimaFermata
        );
    }


}
