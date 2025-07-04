package model.sdata;

import java.io.Serializable;

public class UnicaTratta implements Serializable {
    private Long id;
    private Long trattaId;
    private OrarioTratta orarioTratta;

    public UnicaTratta() {}

    public UnicaTratta(Long id, Long trattaId, OrarioTratta orarioTratta) {
        this.id = id;
        this.trattaId = trattaId;
        this.orarioTratta = orarioTratta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrattaId() {
        return trattaId;
    }

    public void setTrattaId(Long trattaId) {
        this.trattaId = trattaId;
    }

    public OrarioTratta getOrarioTratta() {
        return orarioTratta;
    }

    public void setOrarioTratta(OrarioTratta orarioTratta) {
        this.orarioTratta = orarioTratta;
    }

    @Override
    public String toString() {
        return "UnicaTratta{" +
                "id=" + id +
                ", trattaId=" + trattaId +
                ", orarioTratta=" + (orarioTratta != null ? orarioTratta.toString() : "null") +
                '}';
    }
}
