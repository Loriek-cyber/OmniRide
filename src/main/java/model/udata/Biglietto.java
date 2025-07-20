package model.udata;

import java.sql.Timestamp;

public class Biglietto {
    private Long id;
    private Long id_utente;
    private Long id_tratta;
    private Timestamp dataAquisto;
    private Timestamp dataConvalida;
    private Timestamp dataScadenza;
    private double prezzo;
    private StatoBiglietto stato;
    public  enum StatoBiglietto {ACQUISTATO,CONVALIDATO,SCADUTO,ANNULLATO}

    public Biglietto() {
    }


    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getId_utente() {return id_utente;}
    public void setId_utente(Long id_utente) {this.id_utente = id_utente;}
    public Long getId_tratta() {return id_tratta;}
    public void setId_tratta(Long id_tratta) {this.id_tratta = id_tratta;}
    public Timestamp getDataAquisto() {return dataAquisto;}
    public void setDataAquisto(Timestamp dataAquisto) {this.dataAquisto = dataAquisto;}
    public Timestamp getDataConvalida() {return dataConvalida;}
    public void setDataConvalida(Timestamp dataConvalida) {this.dataConvalida = dataConvalida;}
    public Timestamp getDataScadenza() {return dataScadenza;}
    public void setDataScadenza(Timestamp dataScadenza) {this.dataScadenza = dataScadenza;}
    public double getPrezzo() {return prezzo;}
    public void setPrezzo(double prezzo) {this.prezzo = prezzo;}
    public StatoBiglietto getStato() {return this.stato;}
    public void setStato(StatoBiglietto stato) {this.stato = stato;}
}
