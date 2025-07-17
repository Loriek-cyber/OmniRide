package model.pathfinding;

import model.dao.*;
import model.sdata.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Esempio di come utilizzare il GrafoTrasporti
 */
public class EsempioUtilizzoGrafo {
    
    /**
     * Costruisce il grafo completo del sistema di trasporti
     */
    public static GrafoTrasporti costruisciGrafoCompleto() throws SQLException {
        // Recupera tutte le tratte dal database
        List<Tratta> tutteLeTratte = TrattaDAO.getAll();
        
        // Crea il grafo
        GrafoTrasporti grafo = new GrafoTrasporti();
        grafo.costruisciGrafo(tutteLeTratte);
        
        return grafo;
    }
    
    /**
     * Costruisce un grafo filtrato per orario e giorno
     */
    public static GrafoTrasporti costruisciGrafoFiltrato(String giornoSettimana, int oraCorrente) throws SQLException {


        List<Tratta> tutteLeTratte = TrattaDAO.getAll();
        
        // Filtra le tratte basandosi sugli orari validi
        List<Tratta> tratteFiltrate = new ArrayList<>();
        
        for (Tratta tratta : tutteLeTratte) {
            if (!tratta.isAttiva()) continue;
            
            // Verifica se la tratta ha almeno un orario valido per il giorno specificato
            boolean haOrarioValido = false;
            for (OrarioTratta orario : tratta.getOrari()) {
                if (orario.isAttivo() && orario.isValidoPerGiorno(giornoSettimana)) {
                    // Verifica se l'orario è dopo l'ora corrente
                    int oraPartenza = orario.getOraPartenza().toLocalTime().getHour();
                    if (oraPartenza >= oraCorrente) {
                        haOrarioValido = true;
                        break;
                    }
                }
            }
            
            if (haOrarioValido) {
                tratteFiltrate.add(tratta);
            }
        }
        
        // Costruisci il grafo solo con le tratte filtrate
        GrafoTrasporti grafo = new GrafoTrasporti();
        grafo.costruisciGrafo(tratteFiltrate);
        
        return grafo;
    }
    
    /**
     * Esempio di utilizzo del grafo
     */
    public static void main(String[] args) throws SQLException {
        // Costruisci il grafo completo
        GrafoTrasporti grafo = costruisciGrafoCompleto();
        grafo.stampaInfoGrafo();

    }
}
