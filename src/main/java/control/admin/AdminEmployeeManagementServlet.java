package control.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.DipendentiDAO;
import model.dao.UtenteDAO;
import model.dao.AziendaDAO;
import model.udata.Dipendenti;
import model.udata.Utente;
import model.udata.Azienda;

import java.io.IOException;
import java.util.List;

@WebServlet("/prvAdmin/employees")
public class AdminEmployeeManagementServlet extends HttpServlet {

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
                    listEmployees(request, response);
                    break;
                case "edit":
                    editEmployee(request, response);
                    break;
                case "delete":
                    deleteEmployee(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "toggle":
                    toggleEmployee(request, response);
                    break;
                default:
                    listEmployees(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore: " + e.getMessage());
            listEmployees(request, response);
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
                    createEmployee(request, response);
                    break;
                case "update":
                    updateEmployee(request, response);
                    break;
                case "delete":
                    deleteEmployee(request, response);
                    break;
                case "toggle":
                    toggleEmployee(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/prvAdmin/employees");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore: " + e.getMessage());
            listEmployees(request, response);
        }
    }
    
    private void listEmployees(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Dipendenti> employees = DipendentiDAO.getAll();
            List<Azienda> companies = AziendaDAO.getAll();
            List<Utente> users = UtenteDAO.getAll();
            
            request.setAttribute("employees", employees);
            request.setAttribute("companies", companies);
            request.setAttribute("users", users);
            request.getRequestDispatcher("/prvAdmin/employeeManagement.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento dei dipendenti");
            request.getRequestDispatcher("/prvAdmin/employeeManagement.jsp").forward(request, response);
        }
    }
    
    private void createEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String userEmail = request.getParameter("userEmail");
            String idAziendaStr = request.getParameter("idAzienda");
            String ruolo = request.getParameter("ruolo");
            
            // Validazione base
            if (userEmail == null || userEmail.trim().isEmpty() ||
                idAziendaStr == null || idAziendaStr.trim().isEmpty() ||
                ruolo == null || ruolo.trim().isEmpty()) {
                
                request.setAttribute("error", "Tutti i campi sono obbligatori");
                showCreateForm(request, response);
                return;
            }
            
            // Trova utente per email
            Utente utente = UtenteDAO.findByEmail(userEmail.trim().toLowerCase());
            if (utente == null) {
                request.setAttribute("error", "Utente con email " + userEmail + " non trovato");
                showCreateForm(request, response);
                return;
            }
            
            Long idAzienda = Long.parseLong(idAziendaStr);
            
            // Verifica che l'azienda esista
            Azienda azienda = AziendaDAO.getById(idAzienda);
            if (azienda == null) {
                request.setAttribute("error", "Azienda non trovata");
                showCreateForm(request, response);
                return;
            }
            
            // Verifica che il Dipendenti non esista già
            if (DipendentiDAO.getById(utente.getId(), idAzienda) != null) {
                request.setAttribute("error", "L'utente è già Dipendenti di questa azienda");
                showCreateForm(request, response);
                return;
            }
            
            // Crea nuovo Dipendenti
            Dipendenti newEmployee = new Dipendenti();
            newEmployee.setUtente(utente);
            newEmployee.setAzienda(azienda);
            newEmployee.setLavoro(Dipendenti.Lavoro.valueOf(ruolo));
            newEmployee.setAttivo(true); // Default attivo
            
            DipendentiDAO.create(newEmployee);
            
            request.setAttribute("success", "Dipendenti assunto con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAdmin/employees");
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID azienda non valido");
            showCreateForm(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nell'assunzione del Dipendenti: " + e.getMessage());
            showCreateForm(request, response);
        }
    }
    
    private void updateEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("userId"));
            Long companyId = Long.parseLong(request.getParameter("companyId"));
            String ruolo = request.getParameter("ruolo");
            boolean attivo = "on".equals(request.getParameter("attivo"));
            
            Dipendenti employee = DipendentiDAO.getById(userId, companyId);
            if (employee == null) {
                request.setAttribute("error", "Dipendenti non trovato");
                listEmployees(request, response);
                return;
            }
            
            employee.setLavoro(Dipendenti.Lavoro.valueOf(ruolo));
            employee.setAttivo(attivo);
            
            DipendentiDAO.update(employee);
            
            request.setAttribute("success", "Dipendenti aggiornato con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAdmin/employees");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nell'aggiornamento del Dipendenti: " + e.getMessage());
            listEmployees(request, response);
        }
    }
    
    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("userId"));
            Long companyId = Long.parseLong(request.getParameter("companyId"));
            
            DipendentiDAO.delete(userId, companyId);
            
            request.setAttribute("success", "Dipendenti licenziato con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAdmin/employees");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel licenziamento del Dipendenti: " + e.getMessage());
            listEmployees(request, response);
        }
    }
    
    private void editEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("userId"));
            Long companyId = Long.parseLong(request.getParameter("companyId"));
            
            Dipendenti employee = DipendentiDAO.getById(userId, companyId);
            if (employee == null) {
                request.setAttribute("error", "Dipendenti non trovato");
                listEmployees(request, response);
                return;
            }
            
            List<Azienda> companies = AziendaDAO.getAll();
            List<Utente> users = UtenteDAO.getAll();
            
            request.setAttribute("editEmployee", employee);
            request.setAttribute("companies", companies);
            request.setAttribute("users", users);
            request.getRequestDispatcher("/prvAdmin/editEmployee.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento del Dipendenti");
            listEmployees(request, response);
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Azienda> companies = AziendaDAO.getAll();
            List<Utente> users = UtenteDAO.getAll();
            
            request.setAttribute("companies", companies);
            request.setAttribute("users", users);
            request.getRequestDispatcher("/prvAdmin/createEmployee.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento del form");
            request.getRequestDispatcher("/prvAdmin/createEmployee.jsp").forward(request, response);
        }
    }
    
    private void toggleEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("userId"));
            Long companyId = Long.parseLong(request.getParameter("companyId"));
            
            Dipendenti employee = DipendentiDAO.getById(userId, companyId);
            if (employee != null) {
                employee.setAttivo(!employee.isAttivo());
                DipendentiDAO.update(employee);
                
                String status = employee.isAttivo() ? "riattivato" : "sospeso";
                request.setAttribute("success", "Dipendenti " + status + " con successo!");
            }
            
            response.sendRedirect(request.getContextPath() + "/prvAdmin/employees");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel cambiamento stato del Dipendenti");
            listEmployees(request, response);
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
