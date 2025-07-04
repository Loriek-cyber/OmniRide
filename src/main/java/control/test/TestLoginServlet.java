package control.test;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.UtenteDAO;
import model.udata.Utente;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/test/login")
public class TestLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Test Login - Debug</title></head>");
        out.println("<body>");
        out.println("<h1>Test Database e Login</h1>");
        
        try {
            // Test connessione database
            out.println("<h2>1. Test Connessione Database</h2>");
            UtenteDAO utenteDAO = new UtenteDAO();
            List<Utente> utenti = UtenteDAO.getAllUtenti();
            
            out.println("<p><strong>Connessione: OK</strong></p>");
            out.println("<p>Numero utenti trovati: " + utenti.size() + "</p>");
            
            // Mostra gli utenti
            out.println("<h2>2. Utenti nel Database</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Nome</th><th>Cognome</th><th>Email</th><th>Password Hash</th><th>Ruolo</th></tr>");
            
            for (Utente utente : utenti) {
                out.println("<tr>");
                out.println("<td>" + utente.getId() + "</td>");
                out.println("<td>" + utente.getNome() + "</td>");
                out.println("<td>" + utente.getCognome() + "</td>");
                out.println("<td>" + utente.getEmail() + "</td>");
                out.println("<td>" + (utente.getPasswordHash() != null ? 
                    (utente.getPasswordHash().length() > 20 ? 
                        utente.getPasswordHash().substring(0, 20) + "..." : 
                        utente.getPasswordHash()) : "NULL") + "</td>");
                out.println("<td>" + utente.getRuolo() + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            
            // Test form di login
            out.println("<h2>3. Test Form Login</h2>");
            out.println("<form action='" + req.getContextPath() + "/test/login' method='post'>");
            out.println("<p>");
            out.println("Email: <input type='email' name='email' required><br><br>");
            out.println("Password: <input type='password' name='password' required><br><br>");
            out.println("<input type='submit' value='Test Login'>");
            out.println("</p>");
            out.println("</form>");
            
        } catch (Exception e) {
            out.println("<p><strong>Errore: " + e.getMessage() + "</strong></p>");
            e.printStackTrace();
        }
        
        out.println("</body>");
        out.println("</html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Risultato Test Login</title></head>");
        out.println("<body>");
        out.println("<h1>Risultato Test Login</h1>");
        
        out.println("<p><strong>Email testata:</strong> " + email + "</p>");
        out.println("<p><strong>Password inserita:</strong> [NASCOSTA]</p>");
        
        try {
            UtenteDAO utenteDAO = new UtenteDAO();
            Utente utente = utenteDAO.findByEmail(email);
            
            if (utente == null) {
                out.println("<p style='color: red;'><strong>RISULTATO: Utente non trovato</strong></p>");
            } else {
                out.println("<p><strong>Utente trovato:</strong></p>");
                out.println("<ul>");
                out.println("<li>ID: " + utente.getId() + "</li>");
                out.println("<li>Nome: " + utente.getNome() + " " + utente.getCognome() + "</li>");
                out.println("<li>Email: " + utente.getEmail() + "</li>");
                out.println("<li>Ruolo: " + utente.getRuolo() + "</li>");
                out.println("</ul>");
                
                String passwordHash = utente.getPasswordHash();
                out.println("<p><strong>Password Hash:</strong> " + 
                    (passwordHash != null ? passwordHash.substring(0, Math.min(30, passwordHash.length())) + "..." : "NULL") + "</p>");
                
                // Test verifica password
                boolean passwordCorretta = false;
                String metodoUsato = "";
                
                if (passwordHash != null && !passwordHash.trim().isEmpty()) {
                    if (passwordHash.startsWith("$2a$") || passwordHash.startsWith("$2b$") || passwordHash.startsWith("$2y$")) {
                        // BCrypt hash
                        try {
                            passwordCorretta = BCrypt.checkpw(password, passwordHash);
                            metodoUsato = "BCrypt";
                        } catch (Exception e) {
                            out.println("<p style='color: red;'>Errore BCrypt: " + e.getMessage() + "</p>");
                        }
                    } else {
                        // Password in chiaro
                        passwordCorretta = password.equals(passwordHash);
                        metodoUsato = "Confronto diretto (password in chiaro)";
                    }
                }
                
                out.println("<p><strong>Metodo verifica:</strong> " + metodoUsato + "</p>");
                out.println("<p style='color: " + (passwordCorretta ? "green" : "red") + ";'>");
                out.println("<strong>RISULTATO: " + (passwordCorretta ? "PASSWORD CORRETTA ✓" : "PASSWORD SBAGLIATA ✗") + "</strong>");
                out.println("</p>");
            }
            
        } catch (Exception e) {
            out.println("<p style='color: red;'><strong>Errore durante il test: " + e.getMessage() + "</strong></p>");
            e.printStackTrace();
        }
        
        out.println("<p><a href='" + req.getContextPath() + "/test/login'>← Torna al test</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
