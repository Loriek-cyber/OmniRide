package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.BigliettiDAO;
import model.dao.Carta_CreditoDAO;
import model.udata.Biglietto;
import model.udata.Utente;
import model.udata.CartaCredito;
import control.CarrelloServlet.BigliettoCarrello;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        
// Get current user from session
        Utente utente = (Utente) session.getAttribute("utente");
        boolean isGuest = false;
        
        if (utente == null) {
            // Set guest user attributes
            utente = new Utente();
            utente.setId(-1L); // Special ID for guest users
            isGuest = true;
        }
        
        // Get cart items from session
        @SuppressWarnings("unchecked")
        List<BigliettoCarrello> carrello = (List<BigliettoCarrello>) session.getAttribute("carrello");
        
        if (carrello == null || carrello.isEmpty()) {
            // Empty cart, redirect back to cart
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }
        
        // Retrieve or create the list of Biglietti
        @SuppressWarnings("unchecked")
        List<Biglietto> biglietti = (List<Biglietto>) session.getAttribute("biglietti");
        if (biglietti == null) {
            biglietti = new ArrayList<>();
            session.setAttribute("biglietti", biglietti);
        }
        
        try {
            // Process each item in the cart and convert to Biglietto
            for (BigliettoCarrello item : carrello) {
                for (int i = 0; i < item.getQuantita(); i++) {
                    // Create a new Biglietto for each quantity
                    Biglietto newBiglietto = new Biglietto();
                    
                    // Set basic attributes - ensure user ID is never null
                    Long userId = utente.getId();
                    if (userId == null) {
                        userId = -1L; // Default guest user ID
                    }
                    newBiglietto.setId_utente(userId);
                    newBiglietto.setPrezzo(item.getPrezzo());
                    newBiglietto.setDataAcquisto(LocalTime.now());
                    
                    // Set ticket type - ensure it's never null
                    Biglietto.TipoBiglietto tipo = item.getTipo();
                    if (tipo == null) {
                        tipo = Biglietto.TipoBiglietto.NORMALE; // Default type
                    }
                    newBiglietto.setTipo(tipo);
                    
                    // Tickets purchased are validated and activated immediately
                    newBiglietto.setStato(Biglietto.StatoBiglietto.CONVALIDATO);
                    newBiglietto.setDataConvalida(LocalTime.now());
                    
                    // Set expiry date based on ticket type - usare LocalDateTime per calcoli precisi
                    LocalDateTime nowDateTime = LocalDateTime.now();
                    LocalTime dataFine;
                    
                    switch (tipo) {
                        case GIORNALIERO:
                            dataFine = nowDateTime.plusHours(24).toLocalTime(); // Valid for 24 hours
                            break;
                        case ANNUALE:
                            dataFine = nowDateTime.plusDays(365).toLocalTime(); // Valid for 365 days
                            break;
                        case NORMALE:
                        default:
                            dataFine = nowDateTime.plusHours(4).toLocalTime(); // Valid for 4 hours
                            break;
                    }
                    
                    newBiglietto.setDataFine(dataFine);
                    
                    // Set nome field - required for new database structure
                    String nome = item.getNome(); // Assuming BigliettoCarrello has getNome() method
                    if (nome == null || nome.trim().isEmpty()) {
                        nome = "Biglietto Generico"; // Default nome if not available
                    }
                    newBiglietto.setNome(nome);
                    
                    // Generate formatted ticket code (OM0000001 format)
                    // Use a combination of timestamp and random number to ensure uniqueness
                    long timestamp = System.currentTimeMillis();
                    int uniqueNumber = (int) (timestamp % 9999999) + 1; // Ensures number between 1-9999999
                    String ticketCode = String.format("OM%07d", uniqueNumber);
                    
                    // Set the ticket code in the dedicated field
                    newBiglietto.setCodiceBiglietto(ticketCode);
                    newBiglietto.setNome(nome);
                    
                    // Set lists for tratte - initialize empty lists
                    List<Long> idTratte = new ArrayList<>();
                    List<Integer> numeroFermate = new ArrayList<>();
                    
                    // TODO: In futuro, popolare le tratte dal percorso del carrello
                    // Per ora lasciamo le liste vuote per evitare errori con ID tratte inesistenti
                    
                    newBiglietto.setId_tratte(idTratte);
                    newBiglietto.setNumero_fermate(numeroFermate);
                    
                    // Save to database and get generated ID
                    Long bigliettoId = BigliettiDAO.create(newBiglietto);
                    if (bigliettoId != null) {
                        newBiglietto.setId(bigliettoId);
                        biglietti.add(newBiglietto);
                        System.out.println("[CHECKOUT DEBUG] Biglietto creato con ID: " + bigliettoId + " per utente: " + utente.getId());
                    } else {
                        throw new RuntimeException("Impossibile creare il biglietto nel database");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore durante l'acquisto: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        
// Save ticket IDs in cookies for guests (new approach)
        if (isGuest) {
            // Recupera gli ID esistenti dai cookie
            String existingIdsStr = "";
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("guestTicketIds".equals(cookie.getName())) {
                        existingIdsStr = cookie.getValue();
                        break;
                    }
                }
            }
            
            // Combina gli ID esistenti con i nuovi
            List<Long> guestTicketIds = new ArrayList<>();
            if (!existingIdsStr.isEmpty()) {
                String[] existingIds = existingIdsStr.split(",");
                for (String idStr : existingIds) {
                    try {
                        guestTicketIds.add(Long.parseLong(idStr.trim()));
                    } catch (NumberFormatException e) {
                        // Ignora ID non validi
                    }
                }
            }
            
            // Aggiungi i nuovi ID
            for (Biglietto biglietto : biglietti) {
                guestTicketIds.add(biglietto.getId());
            }
            
            // Salva nel cookie (max 30 giorni)
            String idsString = guestTicketIds.stream()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","));
            
            jakarta.servlet.http.Cookie guestCookie = new jakarta.servlet.http.Cookie("guestTicketIds", idsString);
            guestCookie.setMaxAge(30 * 24 * 60 * 60); // 30 giorni
            guestCookie.setPath("/");
            guestCookie.setHttpOnly(true); // Sicurezza
            response.addCookie(guestCookie);
            
            System.out.println("[CHECKOUT DEBUG] Salvati " + biglietti.size() + " ID biglietti guest nei cookie. Totale: " + guestTicketIds.size());
        }

        // Clear the cart after successful checkout
        carrello.clear();
        session.setAttribute("carrello", carrello);
        
        // Set success message
        session.setAttribute("checkoutSuccess", "Acquisto completato con successo!");
        
        // Redirect based on user type
        if (isGuest) {
            // Per gli utenti guest, passa i biglietti alla pagina di successo per salvarli nel sessionStorage
            request.setAttribute("purchasedTickets", biglietti);
            request.getRequestDispatcher("/guestCheckoutSuccess.jsp").forward(request, response);
        } else {
            // For logged-in users, redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/prvUser/dashboard.jsp");
        }
    }
}

