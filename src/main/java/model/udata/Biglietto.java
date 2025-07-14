package model.udata;

import model.dao.BigliettiDAO;
import model.pathfinding.Percorso;
import model.pathfinding.SegmentoPercorso;
import model.sdata.Tratta;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Biglietto {
    private Long id;
    private Long id_utente;
    private Map<Long, Double> map;
    private LocalTime dataAcquisto;
    private LocalTime dataConvalida;
    private double prezzo;
    private StatoBiglietto stato;

    public  enum StatoBiglietto {ACQUISTATO,CONVALIDATO,SCADUTO}

    public Biglietto() {
    }


    // il map long è all'interno della tratta ovviamente
    public Biglietto(Percorso percorso, Long id_utente, Map<Long, Tratta> trattaMap, StatoBiglietto stato) {
        this.id_utente = id_utente;
        this.dataAcquisto = LocalTime.now();
        this.dataConvalida = LocalTime.now();
        this.map = new HashMap<>();
        List<SegmentoPercorso> lista = percorso.getSegmenti();
        for (SegmentoPercorso seg : lista) {
            //Sezione in cui assoccia a un idLong alla tratta e solamente il segmento
            this.map.put(seg.getId_tratta(), trattaMap.get(seg.getId_tratta()).ncosto(seg.getNumero_fermate()));
        }

        this.stato = stato;
        this.prezzo = percorso.getCosto();

    }

    public Biglietto(Tratta tratta,Long id_utente, StatoBiglietto stato) {

    }

    public boolean IsValid(){
        if(stato == StatoBiglietto.ACQUISTATO || stato == StatoBiglietto.CONVALIDATO){
            return true;
        }
        return false;
    }

    public boolean isOk(Long id_tratta) {
        if(map.containsKey(id_tratta)){
            return true;
        }else{
            BigliettiDAO.delete(this);
            return false;}
    }

    public




    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getId_utente() {return id_utente;}
    public void setId_utente(Long id_utente) {this.id_utente = id_utente;}
    public LocalTime getDataAcquisto() {return dataAcquisto;}
    public void setDataAcquisto(LocalTime dataAcquisto) {this.dataAcquisto = dataAcquisto;}
    public LocalTime getDataConvalida() {return dataConvalida;}
    public void setDataConvalida(LocalTime dataConvalida) {this.dataConvalida = dataConvalida;}
    public double getPrezzo() {return prezzo;}
    public void setPrezzo(double prezzo) {this.prezzo = prezzo;}
    public StatoBiglietto getStato() {return this.stato;}
    public void setStato(StatoBiglietto stato) {this.stato = stato;}
}
