package control;

import error.ErrorPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.FermataDAO;
import model.dao.TrattaDAO;
import model.pathfinding.PathFinding;
import model.sdata.Fermata;
import model.sdata.Percorso;
import model.sdata.Tratta;

import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet(name = "ricercaPercorso", value = "/ricerca-percorso")
public class RicercaPercorsoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Carica tutte le fermate per il form di ricerca
            List<Fermata> fermate = FermataDAO.doRetrieveActive();
            request.setAttribute("fermate", fermate);
            
            // Forward al form di ricerca
            request.getRequestDispatcher("/ricerca-percorso.jsp").forward(request, response);
            
        } catch (SQLException e) {
            handleDatabaseError(request, response, "Errore nel caricamento delle fermate");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Validazione parametri
            ValidationResult validation = validateSearchParameters(request);
            if (!validation.isValid()) {
                handleValidationError(request, response, validation.getErrorMessage());
                return;
            }
            
            // Recupera le fermate
            Fermata partenza = FermataDAO.getById(validation.getPartenzaId());
            Fermata destinazione = FermataDAO.getById(validation.getDestinazioneId());
            
            if (partenza == null || destinazione == null) {
                handleValidationError(request, response, "Fermate selezionate non valide");
                return;
            }
            
            // Recupera tutte le tratte
            List<Tratta> tutteLeTratte = TrattaDAO.getAllTratte();
            
            // Esegui la ricerca del percorso
            PathFinding pathFinding = new PathFinding();
            Percorso percorso = pathFinding.trovaPercorso(
                partenza, 
                destinazione, 
                validation.getOrarioPartenza(), 
                validation.getGiorno(), 
                tutteLeTratte
            );
            
            // Imposta gli attributi per la pagina dei risultati
            request.setAttribute("percorso", percorso);
            request.setAttribute("partenza", partenza);
            request.setAttribute("destinazione", destinazione);
            request.setAttribute("orarioPartenza", validation.getOrarioPartenza());
            request.setAttribute("giorno", validation.getGiorno());
            
            // Aggiungi anche le fermate per permettere una nuova ricerca
            List<Fermata> fermate = FermataDAO.doRetrieveActive();
            request.setAttribute("fermate", fermate);
            
            // Forward alla pagina dei risultati
            request.getRequestDispatcher("/risultati-ricerca.jsp").forward(request, response);
            
        } catch (SQLException e) {
            handleDatabaseError(request, response, "Errore durante la ricerca del percorso");
        } catch (Exception e) {
            handleGenericError(request, response, "Errore imprevisto: " + e.getMessage());
        }
    }

    /**
     * Valida i parametri di ricerca
     */
    private ValidationResult validateSearchParameters(HttpServletRequest request) {
        ValidationResult result = new ValidationResult();
        
        // Validazione fermata di partenza
        String partenzaStr = request.getParameter("partenza");
        if (partenzaStr == null || partenzaStr.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("Seleziona una fermata di partenza");
            return result;
        }
        
        try {
            Long partenzaId = Long.parseLong(partenzaStr);
            result.setPartenzaId(partenzaId);
        } catch (NumberFormatException e) {
            result.setValid(false);
            result.setErrorMessage("Fermata di partenza non valida");
            return result;
        }
        
        // Validazione fermata di destinazione
        String destinazioneStr = request.getParameter("destinazione");
        if (destinazioneStr == null || destinazioneStr.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("Seleziona una fermata di destinazione");
            return result;
        }
        
        try {
            Long destinazioneId = Long.parseLong(destinazioneStr);
            result.setDestinazioneId(destinazioneId);
        } catch (NumberFormatException e) {
            result.setValid(false);
            result.setErrorMessage("Fermata di destinazione non valida");
            return result;
        }
        
        // Verifica che partenza e destinazione siano diverse
        if (result.getPartenzaId().equals(result.getDestinazioneId())) {
            result.setValid(false);
            result.setErrorMessage("Partenza e destinazione devono essere diverse");
            return result;
        }
        
        // Validazione orario di partenza
        String orarioStr = request.getParameter("orarioPartenza");
        if (orarioStr == null || orarioStr.trim().isEmpty()) {
            // Se non specificato, usa l'orario attuale
            result.setOrarioPartenza(LocalTime.now());
        } else {
            try {
                LocalTime orario = LocalTime.parse(orarioStr);
                result.setOrarioPartenza(orario);
            } catch (DateTimeParseException e) {
                result.setValid(false);
                result.setErrorMessage("Formato orario non valido (usa HH:MM)");
                return result;
            }
        }
        
        // Validazione giorno della settimana
        String giornoStr = request.getParameter("giorno");
        if (giornoStr == null || giornoStr.trim().isEmpty()) {
            // Se non specificato, usa il giorno attuale
            result.setGiorno(DayOfWeek.from(java.time.LocalDate.now()));
        } else {
            try {
                DayOfWeek giorno = DayOfWeek.valueOf(giornoStr.toUpperCase());
                result.setGiorno(giorno);
            } catch (IllegalArgumentException e) {
                result.setValid(false);
                result.setErrorMessage("Giorno della settimana non valido");
                return result;
            }
        }
        
        result.setValid(true);
        return result;
    }

    /**
     * Gestione errori di validazione
     */
    private void handleValidationError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        ErrorPage errorPage = new ErrorPage(400, message);
        request.setAttribute("error", errorPage);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    /**
     * Gestione errori del database
     */
    private void handleDatabaseError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        ErrorPage errorPage = new ErrorPage(503, message);
        request.setAttribute("error", errorPage);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    /**
     * Gestione errori generici
     */
    private void handleGenericError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        ErrorPage errorPage = new ErrorPage(500, message);
        request.setAttribute("error", errorPage);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    /**
     * Classe interna per la validazione
     */
    private static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        private Long partenzaId;
        private Long destinazioneId;
        private LocalTime orarioPartenza;
        private DayOfWeek giorno;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public Long getPartenzaId() { return partenzaId; }
        public void setPartenzaId(Long partenzaId) { this.partenzaId = partenzaId; }
        public Long getDestinazioneId() { return destinazioneId; }
        public void setDestinazioneId(Long destinazioneId) { this.destinazioneId = destinazioneId; }
        public LocalTime getOrarioPartenza() { return orarioPartenza; }
        public void setOrarioPartenza(LocalTime orarioPartenza) { this.orarioPartenza = orarioPartenza; }
        public DayOfWeek getGiorno() { return giorno; }
        public void setGiorno(DayOfWeek giorno) { this.giorno = giorno; }
    }
}
