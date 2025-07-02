package model.udata;

import java.sql.Timestamp;

public class Utente {
    private long id;
    private String nome;
    private String cognome;
    private String email;
    private String passwordHash; // Importante: non la password in chiaro
    private Timestamp dataRegistrazione;
    private String ruolo;
    private byte[] avatar;

    // Costruttore vuoto
    public Utente() {
    }

    // Getters e Setters per ogni campo
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCognome() {
        return cognome;
    }
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public Timestamp getDataRegistrazione() {
        return dataRegistrazione;
    }
    public void setDataRegistrazione(Timestamp dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }
    public String getRuolo() {
        return ruolo;
    }
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", ruolo='" + ruolo + '\'' +
                '}';
    }
}
