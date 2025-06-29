package model.sdata;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;
import model.udata.Azienda;

//Classe Di Default per la tratta

public class Tratta {
    private Long id;
    private String nome;
    private Azienda azienda;
    private List<OrarioTratta> orarioTrattaList;
    private List<FermataTratta> fermataTrattaList;

    public Tratta() {}

    public Tratta(Long id, String nome, Azienda azienda, List<OrarioTratta> orarioTrattaList, List<FermataTratta> fermataTrattaList) {
        this.id = id;
        this.nome = nome;
        this.azienda = azienda;
        this.orarioTrattaList = orarioTrattaList;
        this.fermataTrattaList = fermataTrattaList;
    }



    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public Azienda getAzienda() {return azienda;}
    public void setAzienda(Azienda azienda) {this.azienda = azienda;}
    public List<OrarioTratta> getOrarioTrattaList() {return orarioTrattaList;}
    public void setOrarioTrattaList(List<OrarioTratta> orarioTrattaList) {this.orarioTrattaList = orarioTrattaList;}
    public List<FermataTratta> getFermataTrattaList() {return fermataTrattaList;}
    public void setFermataTrattaList(List<FermataTratta> fermataTrattaList) {this.fermataTrattaList = fermataTrattaList;}
}