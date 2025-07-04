package control.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.AziendaDAO;
import model.dao.UtenteDAO;
import model.udata.Azienda;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/registerAzienda")
public class RegisterAziendaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Dati Azienda
        String nomeAzienda = req.getParameter("nomeAzienda");
        String tipoAziendaStr = req.getParameter("tipoAzienda");

        // Dati Utente
        String nome = req.getParameter("nome");
        String cognome = req.getParameter("cognome");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        UtenteDAO utenteDAO = new UtenteDAO();
        if (utenteDAO.findByEmail(email) != null) {
            req.setAttribute("errorMessage", "Un utente con questa email esiste già.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
            return;
        }

        try {
            // 1. Crea l'azienda
            Azienda azienda = new Azienda();
            azienda.setNome(nomeAzienda);
            azienda.setTipo(Azienda.TipoAzienda.valueOf(tipoAziendaStr));
            long idAzienda = AziendaDAO.createAzienda(azienda);

            // 2. Crea l'utente e lo collega all'azienda
            Utente utente = new Utente();
            utente.setNome(nome);
            utente.setCognome(cognome);
            utente.setEmail(email);
            utente.setPasswordHash(password); // Il DAO si occuperà dell'hashing
            utente.setRuolo("azienda");

            boolean utenteCreato = utenteDAO.create(utente);

            if (utenteCreato) {
                // Registrazione completata, reindirizza al login
                resp.sendRedirect(req.getContextPath() + "/login?registration=success");
            } else {
                // Errore nella creazione utente
                req.setAttribute("errorMessage", "Errore durante la creazione dell'account utente. Riprova.");
                req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
                // QUI mancherebbe la logica per cancellare l'azienda appena creata (rollback manuale)
            }

        } catch (SQLException e) {
            throw new ServletException("Errore del database durante la registrazione dell'azienda.", e);
        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", "Tipo di azienda non valido.");
            req.getRequestDispatcher("/register/registerAzienda.jsp").forward(req, resp);
        }
    }
}
