package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.udata.Biglietto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/guest-wallet")
public class GuestWalletServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Recupera i biglietti ospite dalla sessione
        @SuppressWarnings("unchecked")
        Map<String, Biglietto> guestTickets = (Map<String, Biglietto>) 
                (session != null ? session.getAttribute("guestTickets") : null);
        
        if (guestTickets == null) {
            guestTickets = new HashMap<>();
        }
        
        // Passa i biglietti alla JSP
        request.setAttribute("guestTickets", guestTickets);
        
        // Forward alla pagina del portafoglio ospite
        request.getRequestDispatcher("/guest-wallet.jsp").forward(request, response);
    }
}
