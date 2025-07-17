package model.sdata;

import model.udata.Azienda;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


//Classe Di Default per la tratta
public class Tratta implements Serializable {
    private Long id;
    private String nome;
    private Azienda azienda;
    private List<FermataTratta> fermataTrattaList;
    private List<OrarioTratta> orari; // Nuova gestione orari semplificata
    private double costo;
    private boolean attiva; // Nuovo campo per gestire tratte attive/disattive

    public Tratta() {
    }


    //questa è una funzione di verifica visto che le DAO non possono fare un paio di cose
    public void validator(){
        //id non deve essere sempre inserito quindi è ok
        if(nome == null || azienda == null){
            throw  new IllegalStateException("[!ATTRIBUTI CHIAVE] nome o Azienda");
        }

        if(fermataTrattaList == null || fermataTrattaList.isEmpty()){
            throw new IllegalStateException("[!FERMATE] le fermate della tratta sono null");
        }
        //dunno it didn't work before so ok?
        AtomicInteger i = new AtomicInteger();

        fermataTrattaList.forEach(e->{
            e.setSequenza(i.getAndIncrement());
        });

        if(orari == null || orari.isEmpty()){
            throw new IllegalStateException("[!Orari] Errore negli orrari ");
        }

        orari.forEach(e->{
            for(FermataTratta ft : fermataTrattaList){
                if(ft.getTempoProssimaFermata()>=1){
                    e.addnext(ft.getTempoProssimaFermata());
                }
            }
        });

    }


    // Costruttore pulito senza parametri inutilizzati
    public Tratta(Long id, String nome, Azienda azienda, List<FermataTratta> fermataTrattaList,List<OrarioTratta> orari, Double costo) {
        this.id = id;
        this.nome = nome;
        this.azienda = azienda;
        this.fermataTrattaList = fermataTrattaList != null ? fermataTrattaList : new ArrayList<>();
        this.costo = costo;
        this.orari = orari != null ? orari : new ArrayList<>();
    }

    public boolean FermataIN(Fermata fermata) {
        if (fermataTrattaList == null || fermata == null) {
            return false;
        }
        return fermataTrattaList.stream().anyMatch(ft -> ft.getFermata().equals(fermata));
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
    public double getCosto(){return costo;}
    public void setCosto(double costo){this.costo = costo;}
    
    // Nuovi getters e setters per la gestione orari
    public List<OrarioTratta> getOrari() { return orari; }
    public void setOrari(List<OrarioTratta> orari) { this.orari = orari; }
    
    public boolean getAttiva() { return attiva; }
    public void setAttiva(boolean attiva) { this.attiva = attiva; }


    public boolean isAttiva() {return attiva;}

    /**
     * Restituisce l'orario di partenza della tratta.
     * Se ci sono più orari, restituisce il primo disponibile.
     * @return LocalTime dell'orario di partenza o null se non disponibile
     */
    public LocalTime getOrarioPartenza() {
        if (orari != null && !orari.isEmpty()) {
            // Restituisce l'ora di partenza del primo orario disponibile
            Time oraPartenza = orari.get(0).getOraPartenza();
            if (oraPartenza != null) {
                return oraPartenza.toLocalTime();
            }
        }
        return null;
    }
    
    /**
     * Aggiunge un orario alla tratta
     */
    public void addOrario(OrarioTratta orario) {
        if (this.orari == null) {
            this.orari = new ArrayList<>();
        }
        this.orari.add(orario);
    }

    public boolean isIN(Fermata f) {
        for (FermataTratta ft : fermataTrattaList) {
            if (ft.getFermata().equals(f)) {
                return true;
            }
        }
        return false;
    }


    public boolean isAfter(Fermata f1, Fermata f2) {
        if(!isIN(f1) || !isIN(f2)) {
            return false;
        }
        int i=0;
        int j=0;
        for (FermataTratta ft : fermataTrattaList) {
            if (ft.getFermata().equals(f1)) {
                i=ft.getSequenza();
            }
            if(ft.getFermata().equals(f2)) {
                j=ft.getSequenza();
            }
        }
        return i < j;
    }


    /**
     * @return costo per n fermate
     */

    public double ncosto(int n){
        return (costo/fermataTrattaList.size()) * n;
    }

    public int getDurata(){
        int i = 0;
        for (FermataTratta ft : fermataTrattaList) {
            i = ft.getTempoProssimaFermata();
        }
        return i;
    }


    /**
     * Verifica se la tratta ha orari attivi
     */
    public boolean hasOrariAttivi() {
        return orari != null && orari.stream().anyMatch(OrarioTratta::isAttivo);
    }

    @Override
    public String toString() {
        return "Tratta{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", azienda=" + azienda.toString() +
                ", fermataTrattaList=" + fermataTrattaList +
                ", orari=" + orari.toString() +
                ", costo=" + costo +
                ", attiva=" + attiva +
                '}';
    }



}