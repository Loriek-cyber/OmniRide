package model.udata;

import java.sql.Timestamp;

public class Dipendenti {
    private Utente utente;
    private Azienda azienda;
    private Lavoro lavoro;
    private Timestamp dataAssunzione;
    private boolean attivo;

    // Enum allineato ai commenti dello schema DB
    public enum Lavoro {
        AUTISTA, CONTROLLORE, GESTORE, SUPERVISORE
    }

    public Dipendenti() {
        this.attivo = true; // Un nuovo dipendente è attivo di default
    }

    // Getters e Setters
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public Azienda getAzienda() { return azienda; }
    public void setAzienda(Azienda azienda) { this.azienda = azienda; }
    public Lavoro getLavoro() { return lavoro; }
    public void setLavoro(Lavoro lavoro) { this.lavoro = lavoro; }
    public Timestamp getDataAssunzione() { return dataAssunzione; }
    public void setDataAssunzione(Timestamp dataAssunzione) { this.dataAssunzione = dataAssunzione; }
    public boolean isAttivo() { return attivo; }
    public void setAttivo(boolean attivo) { this.attivo = attivo; }
}