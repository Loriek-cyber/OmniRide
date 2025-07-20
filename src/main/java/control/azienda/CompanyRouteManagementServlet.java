package control.azienda;

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
import java.util.stream.Collectors;

@WebServlet("/prvAzienda/routes")
public class CompanyRouteManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Verifica autenticazione azienda
        if (!isCompany(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "list") {
                case "list":
                    listRoutes(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "edit":
                    editRoute(request, response);
                    break;
                case "toggle":
                    toggleRoute(request, response);
                    break;
                case "delete":
                    deleteRoute(request, response);
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
        
        // Verifica autenticazione azienda
        if (!isCompany(session)) {
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
                case "toggle":
                    toggleRoute(request, response);
                    break;
                case "delete":
                    deleteRoute(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/prvAzienda/routes");
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
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata per l'utente corrente");
                request.getRequestDispatcher("/prvAzienda/routeManagement.jsp").forward(request, response);
                return;
            }
            
            // Carica tutte le tratte dell'azienda corrente (incluse quelle inattive)
            List<Tratta> companyRoutes = TrattaDAO.getTratteByAziendaIncludingInactive(companyId);
            
            Azienda company = AziendaDAO.getById(companyId);
            
            request.setAttribute("routes", companyRoutes);
            request.setAttribute("company", company);
            request.setAttribute("companyId", companyId);
            request.getRequestDispatcher("/prvAzienda/routeManagement.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento delle tratte");
            request.getRequestDispatcher("/prvAzienda/routeManagement.jsp").forward(request, response);
        }
    }
    
    private void createRoute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String nome = request.getParameter("nome");
            String costoStr = request.getParameter("costo");
            
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                showCreateForm(request, response);
                return;
            }
            
            // Validazione base
            if (nome == null || nome.trim().isEmpty() ||
                costoStr == null || costoStr.trim().isEmpty()) {
                
                request.setAttribute("error", "Nome tratta e costo sono obbligatori");
                showCreateForm(request, response);
                return;
            }
            
            double costo = Double.parseDouble(costoStr);
            
            if (costo <= 0) {
                request.setAttribute("error", "Il costo deve essere maggiore di zero");
                showCreateForm(request, response);
                return;
            }
            
            // Verifica che non esista già una tratta con lo stesso nome per questa azienda
            List<Tratta> existingRoutes = TrattaDAO.getAll().stream()
                .filter(route -> route.getAzienda().getId().equals(companyId) &&
                               nome.trim().equalsIgnoreCase(route.getNome()))
                .collect(Collectors.toList());
            
            if (!existingRoutes.isEmpty()) {
                request.setAttribute("error", "Esiste già una tratta con questo nome nella tua azienda");
                showCreateForm(request, response);
                return;
            }
            
            // Crea nuova tratta
            Tratta newRoute = new Tratta();
            newRoute.setNome(nome.trim());
            newRoute.setAzienda(AziendaDAO.getById(companyId));
            newRoute.setCosto(costo);
            newRoute.setAttiva(true); // Default attiva
            
            TrattaDAO.create(newRoute);
            
            request.setAttribute("success", "Tratta '" + nome + "' creata con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAzienda/routes");
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Costo non valido");
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
            String costoStr = request.getParameter("costo");
            boolean attiva = "on".equals(request.getParameter("attiva"));
            
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                listRoutes(request, response);
                return;
            }
            
            Tratta route = TrattaDAO.getById(routeId);
            if (route == null || !route.getAzienda().getId().equals(companyId)) {
                request.setAttribute("error", "Tratta non trovata o non appartenente alla tua azienda");
                listRoutes(request, response);
                return;
            }
            
            route.setNome(nome.trim());
            route.setCosto(Double.parseDouble(costoStr));
            route.setAttiva(attiva);
            
            TrattaDAO.update(route);
            
            request.setAttribute("success", "Tratta aggiornata con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAzienda/routes");
            
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
            
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                listRoutes(request, response);
                return;
            }
            
            Tratta route = TrattaDAO.getById(routeId);
            if (route == null || !route.getAzienda().equals(companyId)) {
                request.setAttribute("error", "Tratta non trovata o non appartenente alla tua azienda");
                listRoutes(request, response);
                return;
            }
            
            TrattaDAO.delete(routeId);
            
            request.setAttribute("success", "Tratta eliminata con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAzienda/routes");
            
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
            
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                listRoutes(request, response);
                return;
            }
            
            Tratta route = TrattaDAO.getById(routeId);
            if (route == null || !route.getAzienda().getId().equals(companyId)) {
                request.setAttribute("error", "Tratta non trovata o non appartenente alla tua azienda");
                listRoutes(request, response);
                return;
            }
            
            request.setAttribute("editRoute", route);
            request.getRequestDispatcher("/prvAzienda/editRoute.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento della tratta");
            listRoutes(request, response);
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                listRoutes(request, response);
                return;
            }
            
            request.setAttribute("companyId", companyId);
            request.getRequestDispatcher("/prvAzienda/createRoute.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento del form");
            listRoutes(request, response);
        }
    }
    
    private void toggleRoute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long routeId = Long.parseLong(request.getParameter("id"));
            
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                listRoutes(request, response);
                return;
            }
            
            Tratta route = TrattaDAO.getById(routeId);
            if (route != null && route.getAzienda().getId().equals(companyId)) {
                route.setAttiva(!route.isAttiva());
                TrattaDAO.update(route);
                
                String status = route.isAttiva() ? "attivata" : "disattivata";
                request.setAttribute("success", "Tratta " + status + " con successo!");
            } else {
                request.setAttribute("error", "Tratta non trovata o non appartenente alla tua azienda");
            }
            
            response.sendRedirect(request.getContextPath() + "/prvAzienda/routes");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel cambiamento stato della tratta");
            listRoutes(request, response);
        }
    }
    
    private Utente getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (Utente) session.getAttribute("utente");
    }
    
    private Long getCompanyId(Utente user) {
        try {
            // Logica per associare l'utente azienda all'ID azienda
            // Per ora, assumiamo che l'ID dell'utente corrisponda all'ID dell'azienda
            List<Azienda> companies = AziendaDAO.getAll();
            for (Azienda company : companies) {
                // Cerca l'azienda con lo stesso ID dell'utente o implementa la tua logica
                if (company.getId().equals(user.getId())) {
                    return company.getId();
                }
            }
            // Se non c'è corrispondenza diretta, restituisci la prima azienda (da migliorare)
            return companies.isEmpty() ? null : companies.get(0).getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private boolean isCompany(HttpSession session) {
        if (session == null || session.getAttribute("utente") == null) {
            return false;
        }
        
        Utente utente = (Utente) session.getAttribute("utente");
        return "azienda".equals(utente.getRuolo());
    }
}
