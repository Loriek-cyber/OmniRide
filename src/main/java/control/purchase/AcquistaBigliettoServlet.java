package control.purchase;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.BigliettiDAO;
import model.dao.TrattaDAO;
import model.sdata.Tratta;
import model.udata.Biglietto;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/acquistaBiglietto")
public class AcquistaBigliettoServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Recupera i parametri dalla richiesta
        String trattaIdStr = request.getParameter("trattaId");
        String partenza = request.getParameter("partenza");
        String arrivo = request.getParameter("arrivo");
        String data = request.getParameter("data");
        String orario = request.getParameter("orario");
        String prezzo = request.getParameter("prezzo");
        
        // Se manca l'ID della tratta, reindirizza alla ricerca
        if (trattaIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        
        try {
            Long trattaId = Long.parseLong(trattaIdStr);
            Tratta tratta = TrattaDAO.getById(trattaId);
            
            if (tratta != null) {
                // Passa i dati alla JSP
                request.setAttribute("tratta", tratta);
                request.setAttribute("partenza", partenza);
                request.setAttribute("arrivo", arrivo);
                request.setAttribute("data", data);
                request.setAttribute("orario", orario);
                request.setAttribute("prezzo", prezzo);
                
                // Calcola prezzi per diverse tipologie
                double prezzoBase = Double.parseDouble(prezzo != null ? prezzo : "10.00");
                request.setAttribute("prezzoGiornaliero", prezzoBase * 2.5);
                request.setAttribute("prezzoAR", prezzoBase * 1.8);
                
                request.getRequestDispatcher("/acquistaBiglietto.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/tratte.jsp");
            }
            
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");
        
        // Se l'utente non è loggato, reindirizza al login
        if (utente == null) {
            session.setAttribute("redirectAfterLogin", request.getRequestURI() + "?" + request.getQueryString());
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Recupera i parametri del form
        String trattaIdStr = request.getParameter("trattaId");
        String ticketType = request.getParameter("ticketType");
        String paymentMethod = request.getParameter("paymentMethod");
        String prezzoStr = request.getParameter("prezzo");
        
        try {
            Long trattaId = Long.parseLong(trattaIdStr);
            double prezzo = Double.parseDouble(prezzoStr);
            
            // Crea il nuovo biglietto
            Biglietto biglietto = new Biglietto();
            biglietto.setId_utente(utente.getId());
            biglietto.setPrezzo(prezzo);
            biglietto.setDataAcquisto(LocalTime.now());
            biglietto.setStato(Biglietto.StatoBiglietto.ACQUISTATO);
            
            // Imposta la mappa delle tratte
            Map<Long, Double> mapTratte = new HashMap<>();
            mapTratte.put(trattaId, prezzo);
            biglietto.setMap(mapTratte);
            
            // Salva il biglietto nel database
            Long bigliettoId = BigliettiDAO.create(biglietto);
            
            if (bigliettoId != null) {
                // Acquisto completato con successo
                request.setAttribute("success", true);
                request.setAttribute("bigliettoId", bigliettoId);
                request.setAttribute("ticketCode", generateTicketCode(bigliettoId));
                
                // Invia email di conferma (se implementato)
                // sendConfirmationEmail(utente.getEmail(), biglietto);
                
                request.getRequestDispatcher("/confermaAcquisto.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Errore durante l'acquisto del biglietto");
                request.getRequestDispatcher("/acquistaBiglietto.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Si è verificato un errore durante l'acquisto");
            request.getRequestDispatcher("/acquistaBiglietto.jsp").forward(request, response);
        }
    }
    
    private String generateTicketCode(Long bigliettoId) {
        // Genera un codice biglietto univoco
        return "OMR" + String.format("%08d", bigliettoId);
    }
}
