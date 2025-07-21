package control.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.UtenteDAO;
import model.udata.Utente;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;

@WebServlet("/prvAdmin/users")
public class AdminUserManagementServlet extends HttpServlet {

    // Utilizziamo i metodi statici di BCrypt per l'hashing delle password

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
                    listUsers(request, response);
                    break;
                case "edit":
                    editUser(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                default:
                    listUsers(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore: " + e.getMessage());
            listUsers(request, response);
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
                    createUser(request, response);
                    break;
                case "update":
                    updateUser(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                case "promote":
                    promoteUser(request, response);
                    break;
                case "demote":
                    demoteUser(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/prvAdmin/users");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore: " + e.getMessage());
            listUsers(request, response);
        }
    }
    
    private void listUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Utente> users = UtenteDAO.getAll();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/prvAdmin/userManagement.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento degli utenti");
            request.getRequestDispatcher("/prvAdmin/userManagement.jsp").forward(request, response);
        }
    }
    
    private void createUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String ruolo = request.getParameter("ruolo");
            
            // Validazione base
            if (nome == null || nome.trim().isEmpty() ||
                cognome == null || cognome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                ruolo == null || ruolo.trim().isEmpty()) {
                
                request.setAttribute("error", "Tutti i campi sono obbligatori");
                showCreateForm(request, response);
                return;
            }
            
            // Verifica se email già esiste
            if (UtenteDAO.findByEmail(email) != null) {
                request.setAttribute("error", "Email già in uso");
                showCreateForm(request, response);
                return;
            }
            
            // Crea nuovo utente
            Utente newUser = new Utente();
            newUser.setNome(nome.trim());
            newUser.setCognome(cognome.trim());
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
            newUser.setRuolo(ruolo);
            
            UtenteDAO.create(newUser);
            
            request.setAttribute("success", "Utente creato con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAdmin/users");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nella creazione dell'utente: " + e.getMessage());
            showCreateForm(request, response);
        }
    }
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("id"));
            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String email = request.getParameter("email");
            String ruolo = request.getParameter("ruolo");
            
            Utente user = UtenteDAO.getById(userId);
            if (user == null) {
                request.setAttribute("error", "Utente non trovato");
                listUsers(request, response);
                return;
            }
            
            user.setNome(nome.trim());
            user.setCognome(cognome.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setRuolo(ruolo);
            
            // Se è stata fornita una nuova password
            String newPassword = request.getParameter("password");
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            }
            
            UtenteDAO.update(user);
            
            request.setAttribute("success", "Utente aggiornato con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAdmin/users");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nell'aggiornamento dell'utente: " + e.getMessage());
            listUsers(request, response);
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("id"));
            
            // Non permettere di eliminare se stesso
            HttpSession session = request.getSession(false);
            Utente currentAdmin = (Utente) session.getAttribute("utente");
            if (currentAdmin.getId() ==userId) {
                request.setAttribute("error", "Non puoi eliminare il tuo stesso account!");
                listUsers(request, response);
                return;
            }
            
            UtenteDAO.delete(userId);
            
            request.setAttribute("success", "Utente eliminato con successo!");
            response.sendRedirect(request.getContextPath() + "/prvAdmin/users");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nell'eliminazione dell'utente: " + e.getMessage());
            listUsers(request, response);
        }
    }
    
    private void editUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("id"));
            Utente user = UtenteDAO.getById(userId);
            
            if (user == null) {
                request.setAttribute("error", "Utente non trovato");
                listUsers(request, response);
                return;
            }
            
            request.setAttribute("editUser", user);
            request.getRequestDispatcher("/prvAdmin/editUser.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nel caricamento dell'utente");
            listUsers(request, response);
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/prvAdmin/createUser.jsp").forward(request, response);
    }
    
    private void promoteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("id"));
            Utente user = UtenteDAO.getById(userId);
            
            if (user != null) {
                user.setRuolo("admin");
                UtenteDAO.update(user);
                request.setAttribute("success", "Utente promosso ad amministratore!");
            }
            
            response.sendRedirect(request.getContextPath() + "/prvAdmin/users");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nella promozione dell'utente");
            listUsers(request, response);
        }
    }
    
    private void demoteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getParameter("id"));
            
            // Non permettere di retrocedere se stesso
            HttpSession session = request.getSession(false);
            Utente currentAdmin = (Utente) session.getAttribute("utente");
            if (currentAdmin.getId()==userId) {
                request.setAttribute("error", "Non puoi retrocedere il tuo stesso account!");
                listUsers(request, response);
                return;
            }
            
            Utente user = UtenteDAO.getById(userId);
            if (user != null) {
                user.setRuolo("utente");
                UtenteDAO.update(user);
                request.setAttribute("success", "Amministratore retrocesso ad utente normale!");
            }
            
            response.sendRedirect(request.getContextPath() + "/prvAdmin/users");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nella retrocessione dell'utente");
            listUsers(request, response);
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
