package model.udata;

import model.dao.BigliettiDAO;
import model.pathfinding.Percorso;
import model.pathfinding.SegmentoPercorso;
import model.sdata.Tratta;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Biglietto {
    private Long id;
    private Long id_utente;
    private String nome; // Nome del percorso: {Prima Fermata} - {Ultima Fermata}
    private String codiceBiglietto; // Codice formato OM0000001
    private List<Long> id_tratte;
    private List<Integer> numero_fermate;
    private LocalTime dataAcquisto;
    private LocalTime dataConvalida;
    private LocalTime dataFine;
    private double prezzo;
    private StatoBiglietto stato;
    private TipoBiglietto tipo;

    public enum StatoBiglietto {
        ACQUISTATO, CONVALIDATO, SCADUTO
    }

    public enum TipoBiglietto {
        NORMALE, GIORNALIERO, ANNUALE
    }

    public Biglietto() {
        this.id_tratte = new ArrayList<>();
        this.numero_fermate = new ArrayList<>();
    }

    // Costruttore per biglietto acquistato ma non ancora convalidato
    public Biglietto(Percorso percorso, StatoBiglietto stato, Utente utente) {
        this.id_utente = utente.getId();
        this.dataAcquisto = LocalTime.now();
        this.id_tratte = new ArrayList<>();
        this.numero_fermate = new ArrayList<>();

        percorso.getSegmenti().forEach(segmento -> {
            id_tratte.add(segmento.getId_tratta());
            numero_fermate.add(segmento.getNumero_fermate());
        });

        this.stato = stato;
        this.prezzo = percorso.getCosto();
        this.tipo = TipoBiglietto.NORMALE; // Default tipo
        
        // Costruisci il nome del percorso automaticamente
        this.nome = buildNomeFromPercorso(percorso);

        // Se il biglietto è già convalidato, imposta dataConvalida e dataFine
        if (stato == StatoBiglietto.CONVALIDATO) {
            this.dataConvalida = LocalTime.now();
            // Imposta la data di fine in base al tipo di biglietto
            switch (this.tipo) {
                case GIORNALIERO:
                    this.dataFine = LocalTime.now().plusHours(24); // Valido per 24 ore
                    break;
                case ANNUALE:
                    this.dataFine = LocalTime.now().plusHours(24 * 365); // Valido per 365 giorni
                    break;
                case NORMALE:
                default:
                    this.dataFine = LocalTime.now().plusHours(4); // Valido per 4 ore
                    break;
            }
        }
    }

    /**
     * Costruisce il nome del percorso dal primo all'ultimo fermata
     * @param percorso Il percorso da cui estrarre i nomi delle fermate
     * @return Il nome formattato come "Prima Fermata - Ultima Fermata"
     */
    private String buildNomeFromPercorso(Percorso percorso) {
        if (percorso == null || percorso.getSegmenti() == null || percorso.getSegmenti().isEmpty()) {
            return "Percorso Non Definito";
        }
        
        try {
            // Prendi il primo segmento per la fermata di partenza
            SegmentoPercorso primoSegmento = percorso.getSegmenti().get(0);
            String primaFermata = primoSegmento.getFermataIn() != null ? 
                primoSegmento.getFermataIn().getNome() : "Partenza";
            
            // Prendi l'ultimo segmento per la fermata di arrivo
            SegmentoPercorso ultimoSegmento = percorso.getSegmenti().get(percorso.getSegmenti().size() - 1);
            String ultimaFermata = ultimoSegmento.getFermataOu() != null ? 
                ultimoSegmento.getFermataOu().getNome() : "Arrivo";
            
            return primaFermata + " - " + ultimaFermata;
        } catch (Exception e) {
            // In caso di errore, restituisci un nome di default
            return "Percorso Non Definito";
        }
    }
    
    // Costruttore per biglietto con tipo specifico (automaticamente convalidato)
    public Biglietto(Percorso percorso, TipoBiglietto tipo) {
        this.dataAcquisto = LocalTime.now();
        this.dataConvalida = LocalTime.now();
        this.id_tratte = new ArrayList<>();
        this.numero_fermate = new ArrayList<>();

        percorso.getSegmenti().forEach(segmento -> {
            id_tratte.add(segmento.getId_tratta());
            numero_fermate.add(segmento.getNumero_fermate());
        });

        this.prezzo = percorso.getCosto();
        this.tipo = tipo;
        
        // Costruisci il nome del percorso automaticamente
        this.nome = buildNomeFromPercorso(percorso);

        // Calcola prezzo e durata in base al tipo
        switch (tipo) {
            case GIORNALIERO:
                this.prezzo = this.prezzo * 3; // Abbonamento giornaliero costa 3x quello normale
                this.dataFine = LocalTime.now().plusHours(24); // Valido per 24 ore
                break;
            case ANNUALE:
                this.prezzo = ((this.prezzo * 3) / 2) * 100; // Calcolo per l'annuale
                this.dataFine = LocalTime.now().plusHours(24 * 365); // Valido per 365 giorni
                break;
            case NORMALE:
            default:
                this.dataFine = LocalTime.now().plusHours(4); // Valido per 4 ore
                break;
        }

        this.stato = StatoBiglietto.CONVALIDATO;
    }

    // Verifica se un biglietto è valido
    public boolean verifica() {
        if (dataAcquisto == null || stato == StatoBiglietto.SCADUTO) {
            return false;
        }

        // Se il biglietto non è ancora convalidato, è comunque valido (può essere convalidato)
        if (stato == StatoBiglietto.ACQUISTATO) {
            return true;
        }

        // Se è convalidato, verifica se non è scaduto
        if (stato == StatoBiglietto.CONVALIDATO && dataConvalida != null && dataFine != null) {
            boolean isValid = dataFine.isAfter(LocalTime.now());
            if (!isValid) {
                this.stato = StatoBiglietto.SCADUTO;
            }
            return isValid;
        }

        return false;
    }

    // Attiva/convalida un biglietto
    public boolean attiva() {
        // Verifica se il biglietto può essere attivato
        if (stato == StatoBiglietto.SCADUTO) {
            return false;
        }

        // Se è già convalidato, non può essere riattivato
        if (stato == StatoBiglietto.CONVALIDATO) {
            return false;
        }

        // Attiva il biglietto
        stato = StatoBiglietto.CONVALIDATO;
        dataConvalida = LocalTime.now();

        // Imposta la data di fine in base al tipo
        switch (tipo) {
            case GIORNALIERO:
                dataFine = LocalTime.now().plusHours(24);
                break;
            case ANNUALE:
                dataFine = LocalTime.now().plusHours(24 * 365);
                break;
            case NORMALE:
            default:
                dataFine = LocalTime.now().plusHours(4);
                break;
        }

        return true;
    }

    // Verifica se il biglietto è scaduto e aggiorna lo stato
    public void verificaScadenza() {
        if (stato == StatoBiglietto.CONVALIDATO && dataFine != null) {
            if (dataFine.isBefore(LocalTime.now())) {
                this.stato = StatoBiglietto.SCADUTO;
            }
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_utente() {
        return id_utente;
    }

    public void setId_utente(Long id_utente) {
        this.id_utente = id_utente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodiceBiglietto() {
        return codiceBiglietto;
    }

    public void setCodiceBiglietto(String codiceBiglietto) {
        this.codiceBiglietto = codiceBiglietto;
    }

    public List<Long> getId_tratte() {
        return id_tratte;
    }

    public void setId_tratte(List<Long> id_tratte) {
        this.id_tratte = id_tratte;
    }

    public List<Integer> getNumero_fermate() {
        return numero_fermate;
    }

    public void setNumero_fermate(List<Integer> numero_fermate) {
        this.numero_fermate = numero_fermate;
    }

    public LocalTime getDataAcquisto() {
        return dataAcquisto;
    }

    public void setDataAcquisto(LocalTime dataAcquisto) {
        this.dataAcquisto = dataAcquisto;
    }

    public LocalTime getDataConvalida() {
        return dataConvalida;
    }

    public void setDataConvalida(LocalTime dataConvalida) {
        this.dataConvalida = dataConvalida;
    }

    public LocalTime getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalTime dataFine) {
        this.dataFine = dataFine;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public StatoBiglietto getStato() {
        return stato;
    }

    public void setStato(StatoBiglietto stato) {
        this.stato = stato;
    }

    public TipoBiglietto getTipo() {
        return tipo;
    }

    public void setTipo(TipoBiglietto tipo) {
        this.tipo = tipo;
    }

    // Static method to calculate price based on ticket type
    public static double calcolaPrezzo(double prezzoBase, TipoBiglietto tipo) {
        switch (tipo) {
            case GIORNALIERO:
                return prezzoBase * 3; // Day ticket costs 3x normal price
            case ANNUALE:
                return ((prezzoBase * 3) / 2) * 100; // Annual ticket calculation
            case NORMALE:
            default:
                return prezzoBase; // Normal price
        }
    }
    
    // Static method to get ticket duration in hours based on type
    public static long getDurataOre(TipoBiglietto tipo) {
        switch (tipo) {
            case GIORNALIERO:
                return 24; // Valid for 24 hours
            case ANNUALE:
                return 24 * 365; // Valid for 365 days
            case NORMALE:
            default:
                return 4; // Valid for 4 hours
        }
    }
    
    // Static method to get ticket type description
    public static String getDescrizione(TipoBiglietto tipo) {
        switch (tipo) {
            case GIORNALIERO:
                return "Biglietto Giornaliero (24 ore)";
            case ANNUALE:
                return "Abbonamento Annuale (365 giorni)";
            case NORMALE:
            default:
                return "Biglietto Normale (4 ore)";
        }
    }

    @Override
    public String toString() {
        return "Biglietto{" +
                "id=" + id +
                ", id_utente=" + id_utente +
                ", tipo=" + tipo +
                ", stato=" + stato +
                ", prezzo=" + prezzo +
                ", dataAcquisto=" + dataAcquisto +
                ", dataConvalida=" + dataConvalida +
                ", dataFine=" + dataFine +
                ", tratte=" + id_tratte.size() +
                '}';
    }
}
