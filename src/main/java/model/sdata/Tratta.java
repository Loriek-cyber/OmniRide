package model.sdata;

import model.udata.Azienda;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Classe Di Default per la tratta
public class Tratta {
    private Long id;
    private String nome;
    private Azienda azienda;
    private List<UnicaTratta> unicaTrattaList;
    private List<FermataTratta> fermataTrattaList;
    private double costo;

    public Tratta() {
        this.unicaTrattaList = new ArrayList<>();
        this.fermataTrattaList = new ArrayList<>();
    }

    // Costruttore pulito senza parametri inutilizzati
    public Tratta(Long id, String nome, Azienda azienda, List<UnicaTratta> unicaTrattaList, List<FermataTratta> fermataTrattaList, Double costo) {
        this.id = id;
        this.nome = nome;
        this.azienda = azienda;
        this.unicaTrattaList = unicaTrattaList != null ? unicaTrattaList : new ArrayList<>();
        this.fermataTrattaList = fermataTrattaList != null ? fermataTrattaList : new ArrayList<>();
        this.costo = costo;
    }

    public boolean FermataIN(Fermata fermata) {
        if (fermataTrattaList == null || fermata == null) {
            return false;
        }
        return fermataTrattaList.stream().anyMatch(ft -> ft.getFermata().equals(fermata));
    }

    // Questa fermata verifica se una fermata 1 è prima della fermata 2 nella tratta
    public boolean IsAfter(Fermata f1, Fermata f2) throws Exception {
        if (!FermataIN(f1) || !FermataIN(f2)) {
            throw new Exception("Fermate non trovate nella tratta");
        } else {
            int indexF1 = -1;
            int indexF2 = -1;
            for (int i = 0; i < fermataTrattaList.size(); i++) {
                if (fermataTrattaList.get(i).getFermata().equals(f1)) indexF1 = i;
                if (fermataTrattaList.get(i).getFermata().equals(f2)) indexF2 = i;
                // Piccola ottimizzazione: se li abbiamo trovati entrambi, usciamo dal ciclo
                if (indexF1 != -1 && indexF2 != -1) break;
            }
            return indexF1 < indexF2;
        }
    }

    public long getDistanceForTwoFermate(Fermata f1, Fermata f2) {
        if (fermataTrattaList == null || fermataTrattaList.isEmpty()) {
            throw new IllegalStateException("Lista delle fermate non inizializzata o vuota per questa tratta.");
        }
        int indexF1 = -1;
        int indexF2 = -1;

        // Trova gli indici delle due fermate nella lista della tratta
        for (int i = 0; i < fermataTrattaList.size(); i++) {
            Fermata fermataCorrente = fermataTrattaList.get(i).getFermata();
            if (fermataCorrente.equals(f1)) {
                indexF1 = i;
            }
            if (fermataCorrente.equals(f2)) {
                indexF2 = i;
            }
        }

        // Controlla se le fermate sono state trovate
        if (indexF1 == -1 || indexF2 == -1) {
            throw new IllegalArgumentException("Una o entrambe le fermate non sono state trovate su questa tratta: " + getNome());
        }

        // Controlla se la fermata di partenza precede quella di arrivo
        if (indexF1 >= indexF2) {
            throw new IllegalArgumentException("La fermata di partenza deve precedere la fermata di arrivo sulla tratta.");
        }
        // Calcola la somma dei tempi di percorrenza tra le fermate consecutive
        long tempoTotaleInMinuti = 0;
        for (int i = indexF1; i < indexF2; i++) {
            tempoTotaleInMinuti += fermataTrattaList.get(i).getTempoProssimaFermata();
        }
        return tempoTotaleInMinuti;
    }

    // Getters e Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public Azienda getAzienda() {return azienda;}
    public void setAzienda(Azienda azienda) {this.azienda = azienda;}
    public List<FermataTratta> getFermataTrattaList() {return fermataTrattaList;}
    public void setFermataTrattaList(List<FermataTratta> fermataTrattaList) {this.fermataTrattaList = fermataTrattaList;}
    public List<UnicaTratta> getUnicaTrattaList() {return unicaTrattaList;}
    public void setUnicaTrattaList(List<UnicaTratta> unicaTrattaList) {this.unicaTrattaList = unicaTrattaList;}
    public double getCosto(){return costo;}
    public void setCosto(double costo){this.costo = costo;}


    @Override
    public String toString() {
        return "Tratta{" +
                "id=" + id +
                ", nome='" + nome +
                ", azienda=" + (azienda != null ? azienda.toString() : "null") +
                ", unicaTrattaList=" + (unicaTrattaList != null ? unicaTrattaList.toString() : "null") +
                ", fermataTrattaList=" + (fermataTrattaList != null ? fermataTrattaList.toString() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tratta tratta = (Tratta) o;
        return Objects.equals(id, tratta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}