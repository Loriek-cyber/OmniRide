package control.view;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.TrattaDAO;
import model.sdata.OrarioTratta;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "visualizzaTratte", value = "/visualizzaTratte")
public class VisualizzaTratteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id_tratta = req.getParameter("id");
        Long id;
        if(id_tratta != null) {
            id = Long.parseLong(id_tratta);
        } else {
            id = 0L;
        }
        try {
            // Carica le tratte direttamente dal database
            @SuppressWarnings("unchecked")
            List<Tratta> tratte = (List<Tratta>) getServletContext().getAttribute("tratte");
            if(tratte == null) {
                tratte = TrattaDAO.getAll();
            }
            if(id != 0L) {
                for(Tratta tratta : tratte) {
                    if(tratta.getId() != id) {
                        tratte.remove(tratta);
                    }
                }
            }
            if (tratte != null && !tratte.isEmpty()) {
                // Prepara i dati degli orari per la JSP
                List<List<String>> orariFormattati = new ArrayList<>();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                
                for (Tratta tratta : tratte) {
                    List<String> orariTratta = new ArrayList<>();
                    
                    if (tratta.getOrari() != null) {
                        for (OrarioTratta orario : tratta.getOrari()) {
                            if (orario.isAttivo()) {
                                StringBuilder orarioInfo = new StringBuilder();
                                
                                // Formato: "HH:mm (giorni)"
                                if (orario.getOraPartenza() != null) {
                                    orarioInfo.append(timeFormat.format(orario.getOraPartenza()));
                                }
                                
                                if (orario.getOraArrivo() != null) {
                                    orarioInfo.append(" - ").append(timeFormat.format(orario.getOraArrivo()));
                                }
                                
                                if (orario.getGiorniDescrizione() != null && !orario.getGiorniDescrizione().isEmpty()) {
                                    orarioInfo.append(" (").append(orario.getGiorniDescrizione()).append(")");
                                }
                                
                                orariTratta.add(orarioInfo.toString());
                            }
                        }
                    }
                    
                    if (orariTratta.isEmpty()) {
                        orariTratta.add("Nessun orario disponibile");
                    }
                    
                    orariFormattati.add(orariTratta);
                }
                
                // Imposta gli attributi per la JSP
                req.setAttribute("tratte", tratte);
                req.setAttribute("orariFormattati", orariFormattati);
                
                // Aggiungi informazioni per la ricerca
                req.setAttribute("searchPerformed", true);
                req.setAttribute("resultCount", tratte.size());
                
                System.out.println("[VISUALIZZA_TRATTE] Caricate " + tratte.size() + " tratte");
                
            } else {
                req.setAttribute("tratte", new ArrayList<Tratta>());
                req.setAttribute("orariFormattati", new ArrayList<List<String>>());
                System.out.println("[VISUALIZZA_TRATTE] Nessuna tratta trovata");
            }
            
            req.getRequestDispatcher("/tratte.jsp").forward(req, resp);
            
        } catch (SQLException e) {
            System.err.println("[VISUALIZZA_TRATTE ERROR] Errore nel caricamento delle tratte: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("errore", "Errore nel caricamento delle tratte: " + e.getMessage());
            req.setAttribute("tratte", new ArrayList<Tratta>());
            req.setAttribute("orariFormattati", new ArrayList<List<String>>());
            req.getRequestDispatcher("/tratte.jsp").forward(req, resp);
        }
    }
}
