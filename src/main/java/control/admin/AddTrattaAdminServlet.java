package control.admin;

import error.ErrorPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dao.FermataDAO;
import model.dao.FermataTrattaDAO;
import model.dao.TrattaDAO;
import model.sdata.Fermata;
import model.sdata.FermataTratta;
import model.sdata.Tratta;
import model.udata.Azienda;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "addTrattaAdmin", value = "/prvAdmin/addTratta")
public class AddTrattaAdminServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Recupera tutte le fermate disponibili
            List<Fermata> fermate = FermataDAO.doRetrieveAll();
            req.setAttribute("fermate", fermate);
            req.getRequestDispatcher("/prvAdmin/addTratta.jsp").forward(req, resp);
        } catch (SQLException e) {
            handleDatabaseError(req, resp, "Errore nel recupero delle fermate");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Per l'admin, creiamo un'azienda fittizia o usiamo quella di default
            Azienda aziendaDefault = getDefaultAzienda();
            
            // Validazione dei parametri
            ValidationResult validation = validateParameters(req);
            if (!validation.isValid()) {
                handleValidationError(req, resp, validation.getErrorMessage());
                return;
            }

            // Creazione della tratta
            Tratta nuovaTratta = createTratta(validation, aziendaDefault);
            
            // Salvataggio nel database
            Long trattaId = TrattaDAO.create(nuovaTratta);
            
            // Creazione delle relazioni fermata-tratta
            createFermataTrattaRelations(trattaId, validation.getFermateSelezionate(), validation.getTempiTraFermate());
            
            // Redirect alla pagina di successo
            resp.sendRedirect(req.getContextPath() + "/prvAdmin/admin.jsp?success=tratta_creata");
            
        } catch (SQLException e) {
            handleDatabaseError(req, resp, "Errore durante il salvataggio della tratta");
        } catch (Exception e) {
            handleGenericError(req, resp, "Errore imprevisto: " + e.getMessage());
        }
    }

    /**
     * Ottiene l'azienda di default per l'admin
     */
    private Azienda getDefaultAzienda() {
        // Per l'admin, usiamo un'azienda di sistema
        Azienda azienda = new Azienda();
        azienda.setId(1L); // ID dell'azienda di sistema
        azienda.setNome("Sistema Amministrativo");
        return azienda;
    }

    /**
     * Valida i parametri della richiesta (stesso codice della servlet azienda)
     */
    private ValidationResult validateParameters(HttpServletRequest req) {
        ValidationResult result = new ValidationResult();
        
        // Validazione nome tratta
        String nome = req.getParameter("nome");
        if (nome == null || nome.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("Il nome della tratta è obbligatorio");
            return result;
        }
        result.setNome(nome.trim());
        
        // Validazione costo
        String costoStr = req.getParameter("costo");
        if (costoStr == null || costoStr.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("Il costo della tratta è obbligatorio");
            return result;
        }
        
        try {
            double costo = Double.parseDouble(costoStr);
            if (costo <= 0) {
                result.setValid(false);
                result.setErrorMessage("Il costo deve essere maggiore di zero");
                return result;
            }
            result.setCosto(costo);
        } catch (NumberFormatException e) {
            result.setValid(false);
            result.setErrorMessage("Il costo deve essere un numero valido");
            return result;
        }
        
        // Validazione fermate selezionate
        String[] fermateSelezionate = req.getParameterValues("fermateSelezionate");
        if (fermateSelezionate == null || fermateSelezionate.length < 2) {
            result.setValid(false);
            result.setErrorMessage("Seleziona almeno 2 fermate per creare una tratta");
            return result;
        }
        
        List<Long> fermateIds = new ArrayList<>();
        for (String fermataId : fermateSelezionate) {
            try {
                fermateIds.add(Long.parseLong(fermataId));
            } catch (NumberFormatException e) {
                result.setValid(false);
                result.setErrorMessage("ID fermata non valido: " + fermataId);
                return result;
            }
        }
        result.setFermateSelezionate(fermateIds);
        
        // Validazione tempi tra fermate
        String[] tempiTraFermate = req.getParameterValues("tempiTraFermate");
        if (tempiTraFermate == null || tempiTraFermate.length != fermateIds.size() - 1) {
            result.setValid(false);
            result.setErrorMessage("Specifica i tempi di percorrenza tra le fermate");
            return result;
        }
        
        List<Integer> tempi = new ArrayList<>();
        for (String tempo : tempiTraFermate) {
            try {
                int tempoInt = Integer.parseInt(tempo);
                if (tempoInt <= 0) {
                    result.setValid(false);
                    result.setErrorMessage("I tempi di percorrenza devono essere maggiori di zero");
                    return result;
                }
                tempi.add(tempoInt);
            } catch (NumberFormatException e) {
                result.setValid(false);
                result.setErrorMessage("Tempo di percorrenza non valido: " + tempo);
                return result;
            }
        }
        result.setTempiTraFermate(tempi);
        
        result.setValid(true);
        return result;
    }

    /**
     * Crea l'oggetto Tratta
     */
    private Tratta createTratta(ValidationResult validation, Azienda azienda) {
        Tratta tratta = new Tratta();
        tratta.setNome(validation.getNome());
        tratta.setCosto(validation.getCosto());
        tratta.setAzienda(azienda);
        tratta.setAttiva(true);
        return tratta;
    }

    /**
     * Crea le relazioni fermata-tratta
     */
    private void createFermataTrattaRelations(Long trattaId, List<Long> fermateIds, List<Integer> tempi) throws SQLException {
        for (int i = 0; i < fermateIds.size(); i++) {
            Fermata fermataCorrente = FermataDAO.doRetrieveById(fermateIds.get(i));
            Fermata prossimaFermata = null;
            int tempoProssimaFermata = 0;
            
            if (i < fermateIds.size() - 1) {
                prossimaFermata = FermataDAO.doRetrieveById(fermateIds.get(i + 1));
                tempoProssimaFermata = tempi.get(i);
            }
            
            FermataTratta fermataTratta = new FermataTratta(
                trattaId,
                fermataCorrente,
                tempoProssimaFermata
            );
            
            FermataTrattaDAO.insertFermataTratta(fermataTratta);
        }
    }

    // Metodi di gestione errori (uguali alla servlet azienda)
    private void handleDatabaseError(HttpServletRequest req, HttpServletResponse resp, String message) throws ServletException, IOException {
        ErrorPage errorPage = new ErrorPage(503, message);
        req.setAttribute("error", errorPage);
        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

    private void handleValidationError(HttpServletRequest req, HttpServletResponse resp, String message) throws ServletException, IOException {
        ErrorPage errorPage = new ErrorPage(400, message);
        req.setAttribute("error", errorPage);
        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

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
        private double costo;
        private List<Long> fermateSelezionate;
        private List<Integer> tempiTraFermate;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public double getCosto() { return costo; }
        public void setCosto(double costo) { this.costo = costo; }
        public List<Long> getFermateSelezionate() { return fermateSelezionate; }
        public void setFermateSelezionate(List<Long> fermateSelezionate) { this.fermateSelezionate = fermateSelezionate; }
        public List<Integer> getTempiTraFermate() { return tempiTraFermate; }
        public void setTempiTraFermate(List<Integer> tempiTraFermate) { this.tempiTraFermate = tempiTraFermate; }
    }
}
