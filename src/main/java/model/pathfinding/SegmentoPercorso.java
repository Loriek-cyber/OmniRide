package model.pathfinding;

import model.sdata.Fermata;
import model.sdata.OrarioTratta;
import model.sdata.Tratta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Rappresenta un segmento del percorso, ovvero una porzione di viaggio
 * su una singola tratta tra due fermate specifiche.
 */
public class SegmentoPercorso {
    //rapresentiamo un segmento di un percorso
    private Long id_tratta;
    private Fermata fermataIn;
    private Fermata fermataOu;
    private LocalTime tempo_arrivo;
    private LocalTime tempo_partenza;
    private Integer numero_fermate;

    public SegmentoPercorso() {}

    //Sezionamento




    //getter and setter
    public Long getId_tratta() {
        return id_tratta;
    }

    public void setId_tratta(Long id_tratta) {
        this.id_tratta = id_tratta;
    }

    public Fermata getFermataIn() {
        return fermataIn;
    }

    public void setFermataIn(Fermata fermataIn) {
        this.fermataIn = fermataIn;
    }

    public Fermata getFermataOu() {
        return fermataOu;
    }

    public void setFermataOu(Fermata fermataOu) {
        this.fermataOu = fermataOu;
    }

    public LocalTime getTempo_arrivo() {
        return tempo_arrivo;
    }

    public void setTempo_arrivo(LocalTime tempo_arrivo) {
        this.tempo_arrivo = tempo_arrivo;
    }

    public LocalTime getTempo_partenza() {
        return tempo_partenza;
    }

    public void setTempo_partenza(LocalTime tempo_partenza) {
        this.tempo_partenza = tempo_partenza;
    }

    public Integer getNumero_fermate() {
        return numero_fermate;
    }

    public void setNumero_fermate(Integer numero_fermate) {
        this.numero_fermate = numero_fermate;
    }
}
