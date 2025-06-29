package model.sdata;
import model.util.Geolock;
import java.time.Duration;
import java.util.Objects;

public class Fermata {
    private Long id;
    private String nome;
    private String indirizzo;
    private Coordinate coordinate;
    private TipoFermata tipo;
    private boolean attiva;


    public enum TipoFermata {
        URBANA, EXTRAURBANA,REGIONALE
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
    public Coordinate getCoordinate() { return coordinate; }
    public void setCoordinate(Coordinate coordinate) { this.coordinate = coordinate; }
    public TipoFermata getTipo() { return tipo; }
    public void setTipo(TipoFermata tipo) { this.tipo = tipo; }
    public boolean isAttiva() { return attiva; }
    public void setAttiva(boolean attiva) { this.attiva = attiva; }
    
}
