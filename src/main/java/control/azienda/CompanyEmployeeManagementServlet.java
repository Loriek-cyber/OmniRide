package control.azienda;

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

@WebServlet("/prvAzienda/employees")
public class CompanyEmployeeManagementServlet extends HttpServlet {

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
                    listEmployees(request, response);
                    break;
                case "hire":
                    showHireForm(request, response);
                    break;
                case "fire":
                    fireEmployee(request, response);
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
        
        // Verifica autenticazione azienda
        if (!isCompany(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "hire":
                    hireEmployee(request, response);
                    break;
                case "fire":
                    fireEmployee(request, response);
                    break;
                case "toggle":
                    toggleEmployee(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/prvAzienda/employees");
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
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                request.getRequestDispatcher("/prvAzienda/employeeManagement.jsp").forward(request, response);
                return;
            }
            
            List<Dipendenti> employees = DipendentiDAO.getByAzienda(companyId);
            List<Utente> allUsers = UtenteDAO.getAll();
            
            request.setAttribute("employees", employees);
            request.setAttribute("users", allUsers);
            request.setAttribute("companyId", companyId);
            request.getRequestDispatcher("/prvAzienda/employeeManagement.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento dei dipendenti");
            request.getRequestDispatcher("/prvAzienda/employeeManagement.jsp").forward(request, response);
        }
    }
    
    private void hireEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String userEmail = request.getParameter("userEmail");
            String ruolo = request.getParameter("ruolo");
            
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                listEmployees(request, response);
                return;
            }
            
            // Validazione base
            if (userEmail == null || userEmail.trim().isEmpty() ||
                ruolo == null || ruolo.trim().isEmpty()) {
                
                request.setAttribute("error", "Email utente e ruolo sono obbligatori");
                showHireForm(request, response);
                return;
            }
            
            // Trova utente per email
            Utente utente = UtenteDAO.findByEmail(userEmail.trim().toLowerCase());
            if (utente == null) {
                request.setAttribute("error", "Utente con email " + userEmail + " non trovato");
                showHireForm(request, response);
                return;
            }
            
            // Non permettere di assumere admin o altre aziende
            if ("admin".equals(utente.getRuolo()) || "azienda".equals(utente.getRuolo())) {
                request.setAttribute("error", "Non è possibile assumere amministratori o altre aziende");
                showHireForm(request, response);
                return;
            }
            
            // Verifica che il Dipendenti non esista già
            if (DipendentiDAO.getById(utente.getId(), companyId)!=null) {
                request.setAttribute("error", "L'utente è già Dipendenti della tua azienda");
                showHireForm(request, response);
                return;
            }
            
            // Crea nuovo Dipendenti
            Dipendenti newEmployee = new Dipendenti();
            newEmployee.setUtente(utente);
            newEmployee.setAzienda(AziendaDAO.getById(companyId));
            newEmployee.setLavoro(Dipendenti.Lavoro.valueOf(ruolo));
            newEmployee.setAttivo(true);
            
            DipendentiDAO.create(newEmployee);
            
            request.setAttribute("success", "Dipendenti " + utente.getNome() + " " + utente.getCognome() + " assunto con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAzienda/employees");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nell'assunzione del Dipendenti: " + e.getMessage());
            showHireForm(request, response);
        }
    }
    
    private void fireEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("userId"));
            
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                listEmployees(request, response);
                return;
            }
            
            DipendentiDAO.delete(userId, companyId);
            
            request.setAttribute("success", "Dipendenti licenziato con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAzienda/employees");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel licenziamento del Dipendenti: " + e.getMessage());
            listEmployees(request, response);
        }
    }
    
    private void toggleEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("userId"));
            
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                listEmployees(request, response);
                return;
            }
            
            Dipendenti employee = DipendentiDAO.getById(userId, companyId);
            if (employee != null) {
                employee.setAttivo(!employee.isAttivo());
                DipendentiDAO.update(employee);
                
                String status = employee.isAttivo() ? "riattivato" : "sospeso";
                request.setAttribute("success", "Dipendenti " + status + " con successo!");
            }
            
            response.sendRedirect(request.getContextPath() + "/prvAzienda/employees");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel cambiamento stato del Dipendenti");
            listEmployees(request, response);
        }
    }
    
    private void showHireForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Utente currentUser = getCurrentUser(request);
            Long companyId = getCompanyId(currentUser);
            
            if (companyId == null) {
                request.setAttribute("error", "Azienda non trovata");
                listEmployees(request, response);
                return;
            }
            
            request.setAttribute("companyId", companyId);
            request.getRequestDispatcher("/prvAzienda/hireEmployee.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento del form");
            listEmployees(request, response);
        }
    }
    
    private Utente getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (Utente) session.getAttribute("utente");
    }
    
    private Long getCompanyId(Utente user) {
        try {
            // L'utente azienda ha l'email che corrisponde a un'azienda nel sistema
            // Cerca l'azienda associata a questo utente
            List<Azienda> companies = AziendaDAO.getAll();
            for (Azienda company : companies) {
                // Puoi implementare la logica per associare l'utente all'azienda
                // Per ora, assumiamo che l'ID dell'utente corrisponda all'ID dell'azienda
                // o che ci sia un'altra logica di associazione
                if (company.getId().equals(user.getId())) {
                    return company.getId();
                }
            }
            // Se non c'è corrispondenza diretta, usa il primo ID disponibile (da migliorare)
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
