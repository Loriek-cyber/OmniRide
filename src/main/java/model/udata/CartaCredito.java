package model.udata;

public class CartaCredito {
    private Long id_utente;
    private String nome_intestatario;
    private String numeroCarta;
    private  String Data_scadenza;
    private String cvv;

    public CartaCredito(Long id_utente, String nome_intestatario, String numeroCarta, String data_scadenza, String cvv) {
        this.id_utente = id_utente;
        this.nome_intestatario = nome_intestatario;
        this.numeroCarta = numeroCarta;
        Data_scadenza = data_scadenza;
        this.cvv = cvv;
    }

    public CartaCredito(String nome_intestatario, String numeroCarta, String data_scadenza, String cvv) {
        this.nome_intestatario = nome_intestatario;
        this.numeroCarta = numeroCarta;
        Data_scadenza = data_scadenza;
        this.cvv = cvv;
    }

    public CartaCredito() {
    }

    public Long getId_utente() {
        return id_utente;
    }

    public void setId_utente(Long id_utente) {
        this.id_utente = id_utente;
    }

    public String getNome_intestatario() {
        return nome_intestatario;
    }

    public void setNome_intestatario(String nome_intestatario) {
        this.nome_intestatario = nome_intestatario;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public void setNumeroCarta(String numeroCarta) {
        this.numeroCarta = numeroCarta;
    }

    public String getData_scadenza() {
        return Data_scadenza;
    }

    public void setData_scadenza(String data_scadenza) {
        Data_scadenza = data_scadenza;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
