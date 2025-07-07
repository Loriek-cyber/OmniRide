package model.udata;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Biglietto {
    private Long id;
    private Long idUtente;
    private Long idTratta;
    private Long idOrario; // Aggiunto
    private Timestamp dataAcquisto; // Corretto nome e convenzione
    private Timestamp dataConvalida;
    private Timestamp dataScadenza; // Aggiunto
    private BigDecimal prezzoPagato; // Modificato in BigDecimal per precisione
    private StatoBiglietto stato;

    public enum StatoBiglietto {
        ACQUISTATO, CONVALIDATO, SCADUTO, ANNULLATO
    }

    public Biglietto() {}

    // Getters e Setters standard per tutti i campi
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdUtente() { return idUtente; }
    public void setIdUtente(Long idUtente) { this.idUtente = idUtente; }
    public Long getIdTratta() { return idTratta; }
    public void setIdTratta(Long idTratta) { this.idTratta = idTratta; }
    public Long getIdOrario() { return idOrario; }
    public void setIdOrario(Long idOrario) { this.idOrario = idOrario; }
    public Timestamp getDataAcquisto() { return dataAcquisto; }
    public void setDataAcquisto(Timestamp dataAcquisto) { this.dataAcquisto = dataAcquisto; }
    public Timestamp getDataConvalida() { return dataConvalida; }
    public void setDataConvalida(Timestamp dataConvalida) { this.dataConvalida = dataConvalida; }
    public Timestamp getDataScadenza() { return dataScadenza; }
    public void setDataScadenza(Timestamp dataScadenza) { this.dataScadenza = dataScadenza; }
    public BigDecimal getPrezzoPagato() { return prezzoPagato; }
    public void setPrezzoPagato(BigDecimal prezzoPagato) { this.prezzoPagato = prezzoPagato; }
    public StatoBiglietto getStato() { return stato; } // Corretto
    public void setStato(StatoBiglietto stato) { this.stato = stato; }
}