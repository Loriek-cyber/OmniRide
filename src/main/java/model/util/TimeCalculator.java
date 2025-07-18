package model.util;

import model.sdata.Tratta;
import model.sdata.FermataTratta;
import model.sdata.OrarioTratta;
import model.sdata.Fermata;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class per la gestione ottimizzata del tempo e degli orari nelle tratte.
 * Semplifica il calcolo degli orari e la gestione del tempo nel sistema di pathfinding.
 */
public class TimeCalculator {

    /**
     * Calcola tutti gli orari per una tratta basandosi sull'orario di partenza
     * e sui tempi di percorrenza tra le fermate.
     * 
     * @param tratta La tratta per cui calcolare gli orari
     * @param orarioPartenza L'orario di partenza dal capolinea
     * @return Lista di LocalTime che rappresentano gli orari di arrivo ad ogni fermata
     */
    public static List<LocalTime> calcolaOrariTratta(Tratta tratta, LocalTime orarioPartenza) {
        List<LocalTime> orari = new ArrayList<>();
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        
        if (fermate == null || fermate.isEmpty()) {
            return orari;
        }
        
        LocalTime orarioCorrente = orarioPartenza;
        
        // Aggiungi l'orario di partenza del capolinea
        orari.add(orarioCorrente);
        
        // Calcola gli orari per tutte le fermate successive
        for (int i = 0; i < fermate.size() - 1; i++) {
            FermataTratta fermataCorrente = fermate.get(i);
            int tempoAllaSuccessiva = fermataCorrente.getTempoProssimaFermata();
            
            orarioCorrente = orarioCorrente.plusMinutes(tempoAllaSuccessiva);
            orari.add(orarioCorrente);
        }
        
        return orari;
    }
    
    /**
     * Calcola l'orario di arrivo ad una fermata specifica in una tratta.
     * 
     * @param tratta La tratta
     * @param fermata La fermata di cui calcolare l'orario di arrivo
     * @param orarioPartenza L'orario di partenza dal capolinea
     * @return L'orario di arrivo alla fermata specificata, null se non trovata
     */
    public static LocalTime calcolaOrarioArrivoFermata(Tratta tratta, Fermata fermata, LocalTime orarioPartenza) {
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        
        if (fermate == null || fermate.isEmpty()) {
            return null;
        }
        
        LocalTime orarioCorrente = orarioPartenza;
        
        for (FermataTratta fermataTratta : fermate) {
            if (fermataTratta.getFermata().equals(fermata)) {
                return orarioCorrente;
            }
            orarioCorrente = orarioCorrente.plusMinutes(fermataTratta.getTempoProssimaFermata());
        }
        
        return null; // Fermata non trovata
    }
    
    /**
     * Calcola il tempo totale di percorrenza tra due fermate sulla stessa tratta.
     * 
     * @param tratta La tratta
     * @param fermataPartenza La fermata di partenza
     * @param fermataArrivo La fermata di arrivo
     * @return Il tempo in minuti tra le due fermate, -1 se non valido
     */
    public static int calcolaTempoTraFermate(Tratta tratta, Fermata fermataPartenza, Fermata fermataArrivo) {
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        
        if (fermate == null || fermate.isEmpty()) {
            return -1;
        }
        
        boolean trovataPartenza = false;
        int tempoTotale = 0;
        
        for (FermataTratta fermataTratta : fermate) {
            if (fermataTratta.getFermata().equals(fermataPartenza)) {
                trovataPartenza = true;
                continue;
            }
            
            if (trovataPartenza) {
                if (fermataTratta.getFermata().equals(fermataArrivo)) {
                    return tempoTotale;
                }
                tempoTotale += fermataTratta.getTempoProssimaFermata();
            }
        }
        
        return -1; // Sequenza non valida
    }
    
    /**
     * Trova il prossimo orario disponibile per una tratta dopo un orario specificato.
     * 
     * @param tratta La tratta
     * @param dopoOrario L'orario dopo il quale cercare
     * @return Il prossimo orario disponibile, null se non trovato
     */
    public static LocalTime trovaProssimoOrario(Tratta tratta, LocalTime dopoOrario) {
        List<OrarioTratta> orari = tratta.getOrari();
        
        if (orari == null || orari.isEmpty()) {
            return null;
        }
        
        LocalTime prossimoOrario = null;
        
        for (OrarioTratta orario : orari) {
            if (!orario.isAttivo() || orario.getOraPartenza() == null) {
                continue;
            }
            
            LocalTime orarioPartenza = orario.getOraPartenza().toLocalTime();
            
            if (orarioPartenza.isAfter(dopoOrario)) {
                if (prossimoOrario == null || orarioPartenza.isBefore(prossimoOrario)) {
                    prossimoOrario = orarioPartenza;
                }
            }
        }
        
        return prossimoOrario;
    }
    
    /**
     * Popola automaticamente gli orari di tutte le fermate per tutti gli orari di una tratta.
     * 
     * @param tratta La tratta da processare
     */
    public static void popolaOrariTratta(Tratta tratta) {
        List<OrarioTratta> orari = tratta.getOrari();
        
        if (orari == null || orari.isEmpty()) {
            return;
        }
        
        for (OrarioTratta orario : orari) {
            if (orario.getOraPartenza() == null) {
                continue;
            }
            
            // Resetta la lista degli orari per questo orario
            orario.setListatime(new ArrayList<>());
            
            // Calcola tutti gli orari per questa partenza
            List<LocalTime> orariCalcolati = calcolaOrariTratta(tratta, orario.getOraPartenza().toLocalTime());
            orario.setListatime(orariCalcolati);
        }
    }
    
    /**
     * Calcola l'orario di arrivo ad una fermata specifica dato un orario di partenza
     * da una fermata precedente nella stessa tratta.
     * 
     * @param tratta La tratta
     * @param fermataPartenza La fermata di partenza
     * @param fermataArrivo La fermata di arrivo
     * @param orarioPartenza L'orario di partenza dalla fermata di partenza
     * @return L'orario di arrivo alla fermata di arrivo
     */
    public static LocalTime calcolaOrarioArrivoTraFermate(Tratta tratta, Fermata fermataPartenza, 
                                                          Fermata fermataArrivo, LocalTime orarioPartenza) {
        int tempoPercorrenza = calcolaTempoTraFermate(tratta, fermataPartenza, fermataArrivo);
        
        if (tempoPercorrenza == -1) {
            return null;
        }
        
        return orarioPartenza.plusMinutes(tempoPercorrenza);
    }
    
    /**
     * Verifica se un orario è valido (non nullo e ragionevole).
     * 
     * @param orario L'orario da verificare
     * @return true se l'orario è valido
     */
    public static boolean isOrarioValido(LocalTime orario) {
        return orario != null && orario.isAfter(LocalTime.of(0, 0)) && orario.isBefore(LocalTime.of(23, 59, 59));
    }
}
