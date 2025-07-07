package control.admin;

import error.ErrorPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dao.FermataDAO;
import model.sdata.Coordinate;
import model.sdata.Fermata;
import model.util.Geolock;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "addFermataAdmin", value = "/prvAdmin/addFermata")
public class AddFermataAdminServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Mostra il form per aggiungere una fermata
        req.getRequestDispatcher("/prvAdmin/addFermata.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Validazione dei parametri
            ValidationResult validation = validateParameters(req);
            if (!validation.isValid()) {
                handleValidationError(req, resp, validation.getErrorMessage());
                return;
            }

            // Ottieni coordinate dall'indirizzo
            Coordinate coordinate;
            try {
                coordinate = Geolock.getCoordinatesFromAddress(validation.getIndirizzo());
            } catch (Exception e) {
                handleGeocodingError(req, resp, "Impossibile trovare le coordinate per l'indirizzo specificato");
                return;
            }
            
            // Crea l'oggetto Fermata
            Fermata fermata = createFermata(validation, coordinate);
            
            // Salva nel database
            Long fermataId = FermataDAO.insertFermata(fermata);
            
            if (fermataId != null) {
                // Redirect con messaggio di successo
                resp.sendRedirect(req.getContextPath() + "/prvAdmin/admin.jsp?success=fermata_creata");
            } else {
                handleDatabaseError(req, resp, "Errore durante il salvataggio della fermata");
            }
            
        } catch (SQLException e) {
            handleDatabaseError(req, resp, "Errore del database: " + e.getMessage());
        } catch (Exception e) {
            handleGenericError(req, resp, "Errore imprevisto: " + e.getMessage());
        }
    }

    /**
     * Valida i parametri della richiesta
     */
    private ValidationResult validateParameters(HttpServletRequest req) {
        ValidationResult result = new ValidationResult();
        
        // Validazione nome
        String nome = req.getParameter("nome");
        if (nome == null || nome.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("Il nome della fermata è obbligatorio");
            return result;
        }
        if (nome.length() < 3) {
            result.setValid(false);
            result.setErrorMessage("Il nome della fermata deve contenere almeno 3 caratteri");
            return result;
        }
        result.setNome(nome.trim());
        
        // Validazione indirizzo
        String indirizzo = req.getParameter("indirizzo");
        if (indirizzo == null || indirizzo.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("L'indirizzo della fermata è obbligatorio");
            return result;
        }
        if (indirizzo.length() < 10) {
            result.setValid(false);
            result.setErrorMessage("L'indirizzo deve essere più specifico (almeno 10 caratteri)");
            return result;
        }
        result.setIndirizzo(indirizzo.trim());
        
        // Validazione tipo fermata
        String tipoStr = req.getParameter("tipo");
        if (tipoStr == null || tipoStr.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("Il tipo di fermata è obbligatorio");
            return result;
        }
        
        try {
            Fermata.TipoFermata tipo = Fermata.TipoFermata.valueOf(tipoStr.toUpperCase());
            result.setTipo(tipo);
        } catch (IllegalArgumentException e) {
            result.setValid(false);
            result.setErrorMessage("Tipo di fermata non valido");
            return result;
        }
        
        result.setValid(true);
        return result;
    }

    /**
     * Crea l'oggetto Fermata
     */
    private Fermata createFermata(ValidationResult validation, Coordinate coordinate) {
        Fermata fermata = new Fermata();
        fermata.setNome(validation.getNome());
        fermata.setIndirizzo(validation.getIndirizzo());
        fermata.setCoordinate(coordinate);
        fermata.setTipo(validation.getTipo());
        fermata.setAttiva(true);
        return fermata;
    }

    /**
     * Gestione errori di validazione
     */
    private void handleValidationError(HttpServletRequest req, HttpServletResponse resp, String message) throws ServletException, IOException {
        ErrorPage errorPage = new ErrorPage(400, message);
        req.setAttribute("error", errorPage);
        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

    /**
     * Gestione errori di geocoding
     */
    private void handleGeocodingError(HttpServletRequest req, HttpServletResponse resp, String message) throws ServletException, IOException {
        ErrorPage errorPage = new ErrorPage(422, message);
        req.setAttribute("error", errorPage);
        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

    /**
     * Gestione errori del database
     */
    private void handleDatabaseError(HttpServletRequest req, HttpServletResponse resp, String message) throws ServletException, IOException {
        ErrorPage errorPage = new ErrorPage(503, message);
        req.setAttribute("error", errorPage);
        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

    /**
     * Gestione errori generici
     */
    private void handleGenericError(HttpServletRequest req, HttpServletResponse resp, String message) throws ServletException, IOException {
        ErrorPage errorPage = new ErrorPage(500, message);
        req.setAttribute("error", errorPage);
        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

    /**
     * Classe interna per la validazione
     */
    private static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        private String nome;
        private String indirizzo;
        private Fermata.TipoFermata tipo;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getIndirizzo() { return indirizzo; }
        public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
        public Fermata.TipoFermata getTipo() { return tipo; }
        public void setTipo(Fermata.TipoFermata tipo) { this.tipo = tipo; }
    }
}
