package model.sdata;

import java.util.List;

public class Avvisi {
    private Long id;
    private String Descrizione;
    private List<Long> id_tratte_coinvolte;

    public Avvisi() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getDescrizione() {return Descrizione;}
    public void setDescrizione(String descrizione) {Descrizione = descrizione;}
    public List<Long> getId_tratte_coinvolte() {return id_tratte_coinvolte;}
    public void setId_tratte_coinvolte(List<Long> id_tratte_coinvolte) {this.id_tratte_coinvolte = id_tratte_coinvolte;}
}
