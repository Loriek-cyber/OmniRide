package model.udata;
import model.sdata.*;

public class Azienda {
    private Long id;
    private String nome;
    private TipoAzienda tipo;

    public enum TipoAzienda {
        URBANA, EXTRAURBANA, REGIONALE,NAZIONALE, PUBBLICO
    }

    public Azienda() {}

    public Azienda(Long id, String nome, TipoAzienda tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoAzienda getTipo() { return tipo; }
    public void setTipo(TipoAzienda tipo) { this.tipo = tipo; }
}
