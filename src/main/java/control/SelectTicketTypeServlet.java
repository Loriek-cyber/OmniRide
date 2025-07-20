package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.udata.Biglietto;
import control.CarrelloServlet.BigliettoCarrello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/selectTicketType")
public class SelectTicketTypeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verify required parameters for ticket selection
        String percorsoJson = request.getParameter("percorso");
        String data = request.getParameter("data");
        String orario = request.getParameter("orario");
        String prezzoBaseStr = request.getParameter("prezzo");
        
        // Debug log
        System.out.println("[SELECT_TICKET_TYPE] Parametri ricevuti:");
        System.out.println("  - percorso: " + percorsoJson);
        System.out.println("  - data: " + data);
        System.out.println("  - orario: " + orario);
        System.out.println("  - prezzo: " + prezzoBaseStr);

        if (percorsoJson == null || data == null || orario == null || prezzoBaseStr == null) {
            System.out.println("[SELECT_TICKET_TYPE] Parametri mancanti, redirect a visualizzaTratte");
            response.sendRedirect(request.getContextPath() + "/visualizzaTratte");
            return;
        }

        try {
            double prezzoBase = Double.parseDouble(prezzoBaseStr);

            // Calculate prices as double values for JSP formatting
            double prezzoNormale = Biglietto.calcolaPrezzo(prezzoBase, Biglietto.TipoBiglietto.NORMALE);
            double prezzoGiornaliero = Biglietto.calcolaPrezzo(prezzoBase, Biglietto.TipoBiglietto.GIORNALIERO);
            double prezzoAnnuale = Biglietto.calcolaPrezzo(prezzoBase, Biglietto.TipoBiglietto.ANNUALE);

            // Set attributes for the JSP
            request.setAttribute("percorso", percorsoJson);
            request.setAttribute("data", data);
            request.setAttribute("orario", orario);
            request.setAttribute("prezzoBase", prezzoBase);
            request.setAttribute("prezzoNormale", prezzoNormale);
            request.setAttribute("prezzoGiornaliero", prezzoGiornaliero);
            request.setAttribute("prezzoAnnuale", prezzoAnnuale);

            // Forward to ticket selection page
            request.getRequestDispatcher("/selectTicketType.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/visualizzaTratte");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Get form parameters
        String percorsoJson = request.getParameter("percorso");
        String data = request.getParameter("data");
        String orario = request.getParameter("orario");
        String tipoStr = request.getParameter("tipo");
        String quantitaStr = request.getParameter("quantita");
        String prezzoStr = request.getParameter("prezzo");
        
        if (percorsoJson == null || data == null || orario == null || 
            tipoStr == null || quantitaStr == null || prezzoStr == null) {
            response.sendRedirect(request.getContextPath() + "/visualizzaTratte");
            return;
        }
        
        try {
            Biglietto.TipoBiglietto tipo = Biglietto.TipoBiglietto.valueOf(tipoStr);
            int quantita = Integer.parseInt(quantitaStr);
            double prezzo = Double.parseDouble(prezzoStr);
            
            // Get or create cart
            @SuppressWarnings("unchecked")
            List<BigliettoCarrello> carrello = (List<BigliettoCarrello>) session.getAttribute("carrello");
            if (carrello == null) {
                carrello = new ArrayList<>();
                session.setAttribute("carrello", carrello);
            }
            
            // Always add to cart - simplified logic
            // Check if item already exists in cart
            BigliettoCarrello existing = carrello.stream()
                    .filter(bc -> bc.getData().equals(data) && 
                                 bc.getOrario().equals(orario) && 
                                 bc.getPrezzo() == prezzo &&
                                 bc.getTipo() == tipo)
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                // Update quantity
                existing.setQuantita(existing.getQuantita() + quantita);
            } else {
                // Add new item to cart
                BigliettoCarrello nuovoBiglietto = new BigliettoCarrello();
                nuovoBiglietto.setPercorsoJson(percorsoJson);
                nuovoBiglietto.setData(data);
                nuovoBiglietto.setOrario(orario);
                nuovoBiglietto.setPrezzo(prezzo);
                nuovoBiglietto.setQuantita(quantita);
                nuovoBiglietto.setTipo(tipo);
                carrello.add(nuovoBiglietto);
            }

            session.setAttribute("carrello", carrello);
            response.sendRedirect(request.getContextPath() + "/carrello");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/visualizzaTratte");
        }
    }
}
