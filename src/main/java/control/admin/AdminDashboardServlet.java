package control.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.BigliettiDAO;
import model.dao.UtenteDAO;
import model.dao.TrattaDAO;
import model.dao.AziendaDAO;
import model.udata.Utente;
import model.udata.Biglietto;
import model.sdata.Tratta;
import model.udata.Azienda;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/prvAdmin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Verifica autenticazione
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Utente utente = (Utente) session.getAttribute("utente");
        
        // Verifica che sia effettivamente un admin
        if (!"admin".equals(utente.getRuolo())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso non autorizzato");
            return;
        }
        
        try {
            // Carica statistiche generali
            loadDashboardStatistics(request);
            
            // Carica dati recenti
            loadRecentData(request);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento dei dati: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/prvAdmin/dashboard.jsp").forward(request, response);
    }
    
    private void loadDashboardStatistics(HttpServletRequest request) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Statistiche utenti
            List<Utente> allUsers = UtenteDAO.getAll();
            stats.put("totalUsers", allUsers.size());
            stats.put("totalAdmins", allUsers.stream().mapToInt(u -> "admin".equals(u.getRuolo()) ? 1 : 0).sum());
            stats.put("totalCompanies", allUsers.stream().mapToInt(u -> "azienda".equals(u.getRuolo()) ? 1 : 0).sum());
            stats.put("totalRegularUsers", allUsers.stream().mapToInt(u -> "utente".equals(u.getRuolo()) ? 1 : 0).sum());
            
            // Statistiche biglietti
            List<Biglietto> allTickets = BigliettiDAO.getAll();
            stats.put("totalTickets", allTickets.size());
            stats.put("activeTickets", allTickets.stream().mapToInt(b -> 
                (b.getStato() == Biglietto.StatoBiglietto.ACQUISTATO || 
                 b.getStato() == Biglietto.StatoBiglietto.CONVALIDATO) ? 1 : 0).sum());
            stats.put("expiredTickets", allTickets.stream().mapToInt(b -> 
                b.getStato() == Biglietto.StatoBiglietto.SCADUTO ? 1 : 0).sum());
            
            // Calcola fatturato totale
            double totalRevenue = allTickets.stream().mapToDouble(Biglietto::getPrezzo).sum();
            stats.put("totalRevenue", String.format("€%.2f", totalRevenue));
            
            // Statistiche tratte
            List<Tratta> allRoutes = TrattaDAO.getAll();
            stats.put("totalRoutes", allRoutes.size());
            stats.put("activeRoutes", allRoutes.stream().mapToInt(t -> t.isAttiva() ? 1 : 0).sum());
            
            // Statistiche aziende
            List<Azienda> allCompanies = AziendaDAO.getAll();
            stats.put("totalCompaniesCount", allCompanies.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            stats.put("error", "Errore nel caricamento delle statistiche");
        }
        
        request.setAttribute("stats", stats);
    }
    
    private void loadRecentData(HttpServletRequest request) {
        try {
            // Ultimi 10 utenti registrati
            List<Utente> recentUsers = UtenteDAO.getAll();
            request.setAttribute("recentUsers", recentUsers);
            
            // Ultimi 10 biglietti acquistati
            List<Biglietto> recentTickets = BigliettiDAO.getAll();
            if (recentTickets.size() > 10) {
                recentTickets = recentTickets.subList(0, 10);
            }
            request.setAttribute("recentTickets", recentTickets);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("recentDataError", "Errore nel caricamento dei dati recenti");
        }
    }
}
