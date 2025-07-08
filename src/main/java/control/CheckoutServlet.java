package control;

import error.ErrorPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.BigliettiDAO;
import model.dao.udata.SessioneDAO;
import model.sdata.Tratta;
import model.udata.Biglietto;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@WebServlet(name = "checkout", value = "/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession httpSession = request.getSession(false);
        
        // Controllo autenticazione
        if (httpSession == null || httpSession.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Verifica che ci siano biglietti nel carrello
        @SuppressWarnings("unchecked")
        Map<Long, Tratta> carrello = (Map<Long, Tratta>) httpSession.getAttribute("carrello");
        
        if (carrello == null || carrello.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrello.jsp?error=carrello_vuoto");
            return;
        }
        
        // Calcola il totale
        double totale = 0.0;
        for (Tratta tratta : carrello.values()) {
            totale += tratta.getCosto();
        }
        
        request.setAttribute("carrello", carrello);
        request.setAttribute("totale", totale);
        
        // Forward alla pagina di checkout
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession httpSession = request.getSession(false);
        
        // Controllo autenticazione
        if (httpSession == null || httpSession.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Verifica sessione personalizzata
        String sessionId = (String) httpSession.getAttribute("sessionId");
        if (sessionId != null) {
            try {
                boolean sessioneValida = SessioneDAO.sessioneEsistente(sessionId);
                if (sessioneValida) {
                    SessioneDAO.aggiornaUltimoAccesso(sessionId, Instant.now().getEpochSecond());
                } else {
                    httpSession.invalidate();
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
            } catch (SQLException e) {
                System.err.println("[CHECKOUT ERROR] Errore durante la verifica della sessione: " + e.getMessage());
            }
        }
        
        Utente utente = (Utente) httpSession.getAttribute("utente");
        
        @SuppressWarnings("unchecked")
        Map<Long, Tratta> carrello = (Map<Long, Tratta>) httpSession.getAttribute("carrello");
        
        if (carrello == null || carrello.isEmpty()) {
            handleValidationError(request, response, "Il carrello è vuoto");
            return;
        }
        
        try {
            // Validazione del metodo di pagamento
            ValidationResult validation = validatePaymentParameters(request);
            if (!validation.isValid()) {
                handleValidationError(request, response, validation.getErrorMessage());
                return;
            }
            
            // Processa l'acquisto
            boolean acquistoRiuscito = processaAcquisto(utente, carrello, validation);
            
            if (acquistoRiuscito) {
                // Svuota il carrello
                httpSession.removeAttribute("carrello");
                
                // Redirect alla pagina di successo
                response.sendRedirect(request.getContextPath() + "/biglietti.jsp?success=acquisto_completato");
            } else {
                handleGenericError(request, response, "Errore durante il completamento dell'acquisto");
            }
            
        } catch (SQLException e) {
            handleDatabaseError(request, response, "Errore durante l'acquisto: " + e.getMessage());
        } catch (Exception e) {
            handleGenericError(request, response, "Errore imprevisto: " + e.getMessage());
        }
    }
    
    /**
     * Processa l'acquisto dei biglietti
     */
    private boolean processaAcquisto(Utente utente, Map<Long, Tratta> carrello, ValidationResult validation) 
            throws SQLException {
        
        Timestamp dataAcquisto = new Timestamp(System.currentTimeMillis());
        
        // Crea un biglietto per ogni tratta nel carrello
        for (Tratta tratta : carrello.values()) {
            Biglietto biglietto = new Biglietto();
            biglietto.setId_utente(utente.getId());
            biglietto.setId_tratta(tratta.getId());
            biglietto.setDataAquisto(dataAcquisto);
            biglietto.setPrezzo(tratta.getCosto());
            biglietto.setStato(Biglietto.StatoBiglietto.ACQUISTATO);
            
            Long bigliettoId = BigliettiDAO.insertBiglietto(biglietto);
            if (bigliettoId == null) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Valida i parametri di pagamento
     */
    private ValidationResult validatePaymentParameters(HttpServletRequest request) {
        ValidationResult result = new ValidationResult();
        
        // Validazione metodo di pagamento
        String metodoPagamento = request.getParameter("metodoPagamento");
        if (metodoPagamento == null || metodoPagamento.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("Seleziona un metodo di pagamento");
            return result;
        }
        
        // Validazione carta di credito se necessario
        if ("carta".equals(metodoPagamento)) {
            String numeroCard = request.getParameter("numeroCard");
            String scadenza = request.getParameter("scadenza");
            String cvv = request.getParameter("cvv");
            String nomeCard = request.getParameter("nomeCard");
            
            if (numeroCard == null || numeroCard.trim().isEmpty() ||
                scadenza == null || scadenza.trim().isEmpty() ||
                cvv == null || cvv.trim().isEmpty() ||
                nomeCard == null || nomeCard.trim().isEmpty()) {
                
                result.setValid(false);
                result.setErrorMessage("Compila tutti i campi della carta di credito");
                return result;
            }
            
            // Validazione formato numero carta (semplificata)
            if (!numeroCard.matches("\\d{16}")) {
                result.setValid(false);
                result.setErrorMessage("Il numero della carta deve contenere 16 cifre");
                return result;
            }
            
            // Validazione CVV
            if (!cvv.matches("\\d{3,4}")) {
                result.setValid(false);
                result.setErrorMessage("Il CVV deve contenere 3 o 4 cifre");
                return result;
            }
        }
        
        result.setMetodoPagamento(metodoPagamento);
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
        private String metodoPagamento;
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public String getMetodoPagamento() { return metodoPagamento; }
        public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    }
}
