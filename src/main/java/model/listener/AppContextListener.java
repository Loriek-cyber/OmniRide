package model.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
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

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Caricamento dati dal database
            List<Tratta> tratte = TrattaDAO.getAll();
            List<Fermata> fermate = FermataDAO.getAll();
            List<Avvisi> avvisi = AvvisiDAO.getAll();

            // Creazione della mappa ID → Tratta
            Map<Long, Tratta> trattaMap = new HashMap<>();
            for (Tratta tratta : tratte) {
                trattaMap.put(tratta.getId(), tratta);
            }

            // Salvataggio nel ServletContext
            sce.getServletContext().setAttribute("tratte", tratte);
            sce.getServletContext().setAttribute("fermate", fermate);
            sce.getServletContext().setAttribute("avvisi", avvisi);
            sce.getServletContext().setAttribute("tratteMap", trattaMap);

            //Mappatura completa
            GrafoTrasporti grafo = new GrafoTrasporti();
            grafo.costruisciGrafo(tratte);

            //Salvataggio Mappatura
            sce.getServletContext().setAttribute("grafo", grafo);

            LOGGER.info("Dati inizializzati con successo nel ServletContext.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inizializzazione dei dati nel ServletContext", e);
        }
    }



    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Esegui qui eventuale pulizia, se necessaria
        LOGGER.info("Context distrutto. Risorse liberate.");
    }
}
