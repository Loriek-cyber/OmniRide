package control.search;

import model.sdata.Tratta;
import model.sdata.FermataTratta;
import model.sdata.Fermata;
import model.dao.TrattaDAO;
import model.dao.FermataDAO;
import model.pathfinding.Percorso;
import model.pathfinding.Pathfinder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/ricercaPercorsi")
public class RicercaPercorsiServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Recupera i parametri
        String partenza = request.getParameter("partenza");
        String arrivo = request.getParameter("arrivo");
        String data = request.getParameter("data");
        String orario = request.getParameter("orario");
        
        // Validazione base
        if (partenza == null || arrivo == null || partenza.trim().isEmpty() || arrivo.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/?error=campi_mancanti");
            return;
        }
        
        try {
            // Parse parametri temporali
            LocalTime orarioPartenza = orario != null && !orario.isEmpty() ? 
                LocalTime.parse(orario) : LocalTime.now();
            LocalDate dataViaggio = data != null && !data.isEmpty() ? 
                LocalDate.parse(data) : LocalDate.now();
            
            // Recupera tutte le tratte dal context
            @SuppressWarnings("unchecked")
            List<Tratta> tutteLeTratte = (List<Tratta>) getServletContext().getAttribute("tratte");
            if (tutteLeTratte == null) {
                tutteLeTratte = TrattaDAO.getAll();
            }
            
            List<Percorso> percorsi = new ArrayList<>();
            
            // 1. Cerca prima le tratte dirette
            for (Tratta tratta : tutteLeTratte) {
                if (hasFermateConsecutive(tratta, partenza, arrivo)) {
                    // Trovata tratta diretta - crea percorso con un unico segmento
                    Percorso percorsoDiretto = new Percorso(tratta);
                    percorsi.add(percorsoDiretto);
                }
            }
            
            // 2. Se non ci sono tratte dirette, usa il pathfinding
            if (percorsi.isEmpty()) {
                // Trova le fermate esatte
                Fermata fermataPartenza = null;
                Fermata fermataArrivo = null;
                
                // Cerca le fermate nelle tratte
                for (Tratta t : tutteLeTratte) {
                    for (FermataTratta ft : t.getFermataTrattaList()) {
                        if (fermataPartenza == null && ft.getFermata().getNome().equalsIgnoreCase(partenza)) {
                            fermataPartenza = ft.getFermata();
                        }
                        if (fermataArrivo == null && ft.getFermata().getNome().equalsIgnoreCase(arrivo)) {
                            fermataArrivo = ft.getFermata();
                        }
                    }
                }
                
                // Se abbiamo trovato entrambe le fermate, usa il pathfinding
                if (fermataPartenza != null && fermataArrivo != null) {
                    Percorso percorsoComplesso = Pathfinder.find(
                        fermataPartenza, fermataArrivo, tutteLeTratte, orarioPartenza
                    );
                    if (percorsoComplesso != null) {
                        percorsi.add(percorsoComplesso);
                    }
                }
            }
            
            // Passa i risultati alla JSP
            request.setAttribute("percorsi", percorsi);
            request.setAttribute("partenza", partenza);
            request.setAttribute("arrivo", arrivo);
            request.setAttribute("data", data);
            request.setAttribute("orario", orario);
            
            // Forward alla pagina risultati
            request.getRequestDispatcher("/risultatiRicerca.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/?error=errore_ricerca");
        }
    }
    
    /**
     * Verifica se una tratta contiene due fermate consecutive
     */
    private boolean hasFermateConsecutive(Tratta tratta, String partenza, String arrivo) {
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        
        for (int i = 0; i < fermate.size() - 1; i++) {
            String nomeCorrente = fermate.get(i).getFermata().getNome();
            String nomeSuccessiva = fermate.get(i + 1).getFermata().getNome();
            
            if (nomeCorrente.equalsIgnoreCase(partenza) && nomeSuccessiva.equalsIgnoreCase(arrivo)) {
                return true;
            }
        }
        
        // Verifica anche se contiene entrambe le fermate (non necessariamente consecutive)
        boolean hasPartenza = false;
        boolean hasArrivo = false;
        int indexPartenza = -1;
        int indexArrivo = -1;
        
        for (int i = 0; i < fermate.size(); i++) {
            String nome = fermate.get(i).getFermata().getNome();
            if (nome.equalsIgnoreCase(partenza)) {
                hasPartenza = true;
                indexPartenza = i;
            }
            if (nome.equalsIgnoreCase(arrivo)) {
                hasArrivo = true;
                indexArrivo = i;
            }
        }
        
        // Ritorna true se ha entrambe le fermate e arrivo viene dopo partenza
        return hasPartenza && hasArrivo && indexArrivo > indexPartenza;
    }
}
