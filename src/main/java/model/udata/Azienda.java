package model.udata;
import model.sdata.*;

import java.io.Serializable;

public class Azienda implements Serializable {
    private Long id;
    private String nome;
    private String tipo;



    public Azienda() {}

    public Azienda(Long id, String nome, String tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
