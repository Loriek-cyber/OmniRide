package model.udata;

public class Dipendenti {
    private Utente utente;
    private Azienda azienda;
    private Lavoro lavoro;

    public enum  Lavoro{
        CONTROLLORE, AUTISTA,GESTORE
    }

    public Dipendenti(){

    }

    public Utente getUtente() {return utente;}
    public void setUtente(Utente utente) {this.utente = utente;}
    public Azienda getAzienda() {return azienda;}
    public void setAzienda(Azienda azienda) {this.azienda = azienda;}
    public Lavoro getLavoro() {return lavoro;}
    public void setLavoro(Lavoro lavoro) {this.lavoro = lavoro;}


}
