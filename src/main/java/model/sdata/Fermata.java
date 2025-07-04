package model.sdata;

import java.io.Serializable;
import java.util.Objects;

public class Fermata implements Serializable {
    private Long id;
    private String nome;
    private String indirizzo;
    private Coordinate coordinate;
    private TipoFermata tipo;
    private boolean attiva;


    public enum TipoFermata {
        URBANA, EXTRAURBANA, REGIONALE, STAZIONE , FERMATA_NORMALE
    }

    public Fermata() {
    }

    public Fermata(Long id, String nome, String indirizzo, Coordinate coordinate) {
        this.id = id;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.coordinate = coordinate;
        this.attiva = true;
        this.tipo = TipoFermata.URBANA;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    public Coordinate getCoordinate() { return coordinate; }
    public void setCoordinate(Coordinate coordinate) { this.coordinate = coordinate; }
    public TipoFermata getTipo() { return tipo; }
    public void setTipo(TipoFermata tipo) { this.tipo = tipo; }
    public boolean isAttiva() { return attiva; }
    public void setAttiva(boolean attiva) { this.attiva = attiva; }

    /**
     * Due oggetti Fermata sono considerati uguali se hanno lo stesso ID.
     * Questo è fondamentale per il corretto funzionamento di mappe e set.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fermata fermata = (Fermata) o;
        return Objects.equals(id, fermata.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Fermata{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", coordinate=" + (coordinate != null ? coordinate.toString() : "null") +
                ", tipo=" + tipo +
                ", attiva=" + attiva +
                '}'+"\n";
    }
}