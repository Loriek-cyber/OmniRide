package control.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.TrattaDAO;
import model.dao.AziendaDAO;
import model.sdata.Tratta;
import model.udata.Azienda;
import model.udata.Utente;

import java.io.IOException;
import java.util.List;

@WebServlet("/prvAdmin/routes")
public class AdminRouteManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Verifica autenticazione admin
        if (!isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "list") {
                case "list":
                    listRoutes(request, response);
                    break;
                case "edit":
                    editRoute(request, response);
                    break;
                case "delete":
                    deleteRoute(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "toggle":
                    toggleRoute(request, response);
                    break;
                default:
                    listRoutes(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore: " + e.getMessage());
            listRoutes(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Verifica autenticazione admin
        if (!isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "create":
                    createRoute(request, response);
                    break;
                case "update":
                    updateRoute(request, response);
                    break;
                case "delete":
                    deleteRoute(request, response);
                    break;
                case "toggle":
                    toggleRoute(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/prvAdmin/routes");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore: " + e.getMessage());
            listRoutes(request, response);
        }
    }
    
    private void listRoutes(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Carica tutte le tratte incluse quelle inattive per la gestione admin
            List<Tratta> routes = TrattaDAO.getAllIncludingInactive();
            List<Azienda> companies = AziendaDAO.getAll();
            
            request.setAttribute("routes", routes);
            request.setAttribute("companies", companies);
            
            request.getRequestDispatcher("/prvAdmin/routeManagement.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento delle tratte");
            request.getRequestDispatcher("/prvAdmin/routeManagement.jsp").forward(request, response);
        }
    }
    
    private void createRoute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String nome = request.getParameter("nome");
            String idAziendaStr = request.getParameter("idAzienda");
            String costoStr = request.getParameter("costo");
            
            // Validazione base
            if (nome == null || nome.trim().isEmpty() ||
                idAziendaStr == null || idAziendaStr.trim().isEmpty() ||
                costoStr == null || costoStr.trim().isEmpty()) {
                
                request.setAttribute("error", "Tutti i campi sono obbligatori");
                showCreateForm(request, response);
                return;
            }
            
            Long idAzienda = Long.parseLong(idAziendaStr);
            double costo = Double.parseDouble(costoStr);
            
            // Verifica che l'azienda esista
            Azienda azienda = AziendaDAO.getById(idAzienda);
            if (azienda == null) {
                request.setAttribute("error", "Azienda non trovata");
                showCreateForm(request, response);
                return;
            }
            
            // Crea nuova tratta
            Tratta newRoute = new Tratta();
            newRoute.setNome(nome.trim());
            newRoute.setAzienda(azienda);
            newRoute.setCosto(costo);
            newRoute.setAttiva(true); // Default attiva
            
            TrattaDAO.create(newRoute);
            
            request.setAttribute("success", "Tratta creata con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAdmin/routes");
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Costo o ID azienda non validi");
            showCreateForm(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nella creazione della tratta: " + e.getMessage());
            showCreateForm(request, response);
        }
    }
    
    private void updateRoute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long routeId = Long.parseLong(request.getParameter("id"));
            String nome = request.getParameter("nome");
            String idAziendaStr = request.getParameter("idAzienda");
            String costoStr = request.getParameter("costo");
            boolean attiva = "on".equals(request.getParameter("attiva"));
            
            Tratta route = TrattaDAO.getById(routeId);
            if (route == null) {
                request.setAttribute("error", "Tratta non trovata");
                listRoutes(request, response);
                return;
            }
            
            route.setNome(nome.trim());
            route.setAzienda(AziendaDAO.getById(Long.parseLong(idAziendaStr)));
            route.setCosto(Double.parseDouble(costoStr));
            route.setAttiva(attiva);
            
            TrattaDAO.update(route);
            
            request.setAttribute("success", "Tratta aggiornata con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAdmin/routes");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nell'aggiornamento della tratta: " + e.getMessage());
            listRoutes(request, response);
        }
    }
    
    private void deleteRoute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long routeId = Long.parseLong(request.getParameter("id"));
            
            TrattaDAO.delete(routeId);
            
            request.setAttribute("success", "Tratta eliminata con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAdmin/routes");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nell'eliminazione della tratta: " + e.getMessage());
            listRoutes(request, response);
        }
    }
    
    private void editRoute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long routeId = Long.parseLong(request.getParameter("id"));
            Tratta route = TrattaDAO.getById(routeId);
            
            if (route == null) {
                request.setAttribute("error", "Tratta non trovata");
                listRoutes(request, response);
                return;
            }
            
            List<Azienda> companies = AziendaDAO.getAll();
            request.setAttribute("editRoute", route);
            request.setAttribute("companies", companies);
            request.getRequestDispatcher("/prvAdmin/editRoute.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento della tratta");
            listRoutes(request, response);
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Azienda> companies = AziendaDAO.getAll();
            request.setAttribute("companies", companies);
            request.getRequestDispatcher("/prvAdmin/createRoute.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento del form");
            request.getRequestDispatcher("/prvAdmin/createRoute.jsp").forward(request, response);
        }
    }
    
    private void toggleRoute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long routeId = Long.parseLong(request.getParameter("id"));
            Tratta route = TrattaDAO.getById(routeId);
            
            if (route != null) {
                route.setAttiva(!route.isAttiva());
                TrattaDAO.update(route);
                
                String status = route.isAttiva() ? "attivata" : "disattivata";
                request.setAttribute("success", "Tratta " + status + " con successo!");
            }
            
            response.sendRedirect(request.getContextPath() + "/prvAdmin/routes");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel cambiamento stato della tratta");
            listRoutes(request, response);
        }
    }
    
    private boolean isAdmin(HttpSession session) {
        if (session == null || session.getAttribute("utente") == null) {
            return false;
        }
        
        Utente utente = (Utente) session.getAttribute("utente");
        return "admin".equals(utente.getRuolo());
    }
}
