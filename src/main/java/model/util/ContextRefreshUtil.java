package model.util;

import jakarta.servlet.ServletContext;
import model.dao.AvvisiDAO;
import model.dao.FermataDAO;
import model.dao.TrattaDAO;
import model.pathfinding.GrafoTrasporti;
import model.sdata.Avvisi;
import model.sdata.Fermata;
import model.sdata.Tratta;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextRefreshUtil {
    
    private static final Logger LOGGER = Logger.getLogger(ContextRefreshUtil.class.getName());
    
    /**
     * Aggiorna tutti i dati nel ServletContext
     */
    public static void refreshAllData(ServletContext context) throws SQLException {
        LOGGER.info("Inizio aggiornamento completo del ServletContext");
        
        // Caricamento dati dal database
        List<Tratta> tratte = TrattaDAO.getAll();
        List<Fermata> fermate = FermataDAO.getAll();
        List<Avvisi> avvisi = AvvisiDAO.getAll();

        // Creazione della mappa ID → Tratta
        Map<Long, Tratta> trattaMap = new HashMap<>();
        for (Tratta tratta : tratte) {
            trattaMap.put(tratta.getId(), tratta);
        }

        // Aggiornamento del ServletContext
        context.setAttribute("tratte", tratte);
        context.setAttribute("fermate", fermate);
        context.setAttribute("avvisi", avvisi);
        context.setAttribute("tratteMap", trattaMap);

        // Ricostruzione del grafo
        GrafoTrasporti grafo = new GrafoTrasporti();
        grafo.costruisciGrafo(tratte);
        context.setAttribute("grafo", grafo);
        
        LOGGER.info("Aggiornamento completo del ServletContext completato");
    }
    
    /**
     * Aggiorna solo le tratte nel ServletContext
     */
    public static void refreshTratte(ServletContext context) throws SQLException {
        LOGGER.info("Aggiornamento tratte nel ServletContext");
        
        List<Tratta> tratte = TrattaDAO.getAll();
        
        // Aggiorna mappa tratte
        Map<Long, Tratta> trattaMap = new HashMap<>();
        for (Tratta tratta : tratte) {
            trattaMap.put(tratta.getId(), tratta);
        }
        
        context.setAttribute("tratte", tratte);
        context.setAttribute("tratteMap", trattaMap);
        
        // Ricostruisci il grafo
        GrafoTrasporti grafo = new GrafoTrasporti();
        grafo.costruisciGrafo(tratte);
        context.setAttribute("grafo", grafo);
        
        LOGGER.info("Aggiornamento tratte completato");
    }
    
    /**
     * Aggiorna solo le fermate nel ServletContext
     */
    public static void refreshFermate(ServletContext context) throws SQLException {
        LOGGER.info("Aggiornamento fermate nel ServletContext");
        
        List<Fermata> fermate = FermataDAO.getAll();
        context.setAttribute("fermate", fermate);
        
        LOGGER.info("Aggiornamento fermate completato");
    }
    
    /**
     * Aggiorna solo gli avvisi nel ServletContext
     */
    public static void refreshAvvisi(ServletContext context) throws SQLException {
        LOGGER.info("Aggiornamento avvisi nel ServletContext");
        
        List<Avvisi> avvisi = AvvisiDAO.getAll();
        context.setAttribute("avvisi", avvisi);
        
        LOGGER.info("Aggiornamento avvisi completato");
    }
}
