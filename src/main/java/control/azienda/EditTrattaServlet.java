package control.azienda;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.AziendaDAO;
import model.dao.OrarioTrattaDAO;
import model.dao.TrattaDAO;
import model.sdata.OrarioTratta;
import model.sdata.Tratta;
import model.udata.Azienda;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "editTratta", value = "/prvAzienda/editTratta")
public class EditTrattaServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(EditTrattaServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        String trattaIdStr = req.getParameter("id");
        
        if (trattaIdStr == null) {
            req.setAttribute("errore", "ID tratta non specificato");
            req.getRequestDispatcher("/prvAzienda/gestisciTratte").forward(req, resp);
            return;
        }

        try {
            Long trattaId = Long.parseLong(trattaIdStr);
            
            // Recupera l'azienda dell'utente
            Azienda azienda = AziendaDAO.fromIDutente(utente.getId());
            if (azienda == null) {
                req.setAttribute("errore", "Azienda non trovata");
                req.getRequestDispatcher("/").forward(req, resp);
                return;
            }
            
            // Recupera la tratta
            Tratta tratta = TrattaDAO.getById(trattaId);
            if (tratta == null) {
                req.setAttribute("errore", "Tratta non trovata");
                req.getRequestDispatcher("/prvAzienda/gestisciTratte").forward(req, resp);
                return;
            }
            
            // Verifica che la tratta appartenga all'azienda dell'utente
            if (!tratta.getAzienda().getId().equals(azienda.getId())) {
                req.setAttribute("errore", "Non hai i permessi per modificare questa tratta");
                req.getRequestDispatcher("/prvAzienda/gestisciTratte").forward(req, resp);
                return;
            }
            
            req.setAttribute("tratta", tratta);
            req.getRequestDispatcher("/prvAzienda/editTratta.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            req.setAttribute("errore", "ID tratta non valido");
            req.getRequestDispatcher("/prvAzienda/gestisciTratte").forward(req, resp);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero della tratta", e);
            req.setAttribute("errore", "Errore nel recupero della tratta: " + e.getMessage());
            req.getRequestDispatcher("/").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utente non autenticato");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        String trattaIdStr = req.getParameter("trattaId");
        String nome = req.getParameter("nome");
        String costoStr = req.getParameter("costo");
        String attivaStr = req.getParameter("attiva");
        
        // Nuovi orari (opzionali)
        String[] orariInizioStr = req.getParameterValues("orariInizio");
        String[] giorni = req.getParameterValues("giorni");

        if (trattaIdStr == null) {
            req.setAttribute("errore", "ID tratta non specificato");
            doGet(req, resp);
            return;
        }

        try {
            Long trattaId = Long.parseLong(trattaIdStr);
            
            // Recupera l'azienda dell'utente
            Azienda azienda = AziendaDAO.fromIDutente(utente.getId());
            if (azienda == null) {
                req.setAttribute("errore", "Azienda non trovata");
                doGet(req, resp);
                return;
            }
            
            // Recupera la tratta
            Tratta tratta = TrattaDAO.getById(trattaId);
            if (tratta == null) {
                req.setAttribute("errore", "Tratta non trovata");
                doGet(req, resp);
                return;
            }
            
            // Verifica che la tratta appartenga all'azienda dell'utente
            if (!tratta.getAzienda().getId().equals(azienda.getId())) {
                req.setAttribute("errore", "Non hai i permessi per modificare questa tratta");
                doGet(req, resp);
                return;
            }
            
            // Valida i parametri
            if (nome == null || nome.trim().isEmpty()) {
                req.setAttribute("errore", "Nome tratta obbligatorio");
                doGet(req, resp);
                return;
            }
            
            double costo;
            try {
                costo = Double.parseDouble(costoStr);
                if (costo <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                req.setAttribute("errore", "Costo non valido");
                doGet(req, resp);
                return;
            }
            
            boolean attiva = Boolean.parseBoolean(attivaStr);
            
            // Aggiorna la tratta
            tratta.setNome(nome.trim());
            tratta.setCosto(costo);
            tratta.setAttiva(attiva);
            
            boolean updateSuccess = TrattaDAO.update(tratta);
            if (!updateSuccess) {
                req.setAttribute("errore", "Errore nell'aggiornamento della tratta");
                doGet(req, resp);
                return;
            }
            
            // Aggiungi nuovi orari se specificati
            if (orariInizioStr != null && orariInizioStr.length > 0 && giorni != null && giorni.length > 0) {
                String giorniStr = parseGiorni(giorni);
                
                // Usa un Set per rimuovere automaticamente i duplicati
                Set<String> orariUnici = new LinkedHashSet<>(Arrays.asList(orariInizioStr));
                
                for (String orarioStr : orariUnici) {
                    if (orarioStr != null && !orarioStr.trim().isEmpty()) {
                        try {
                            orarioStr = orarioStr.trim();
                            LocalTime orarioLocal = LocalTime.parse(orarioStr);
                            Time orarioTime = Time.valueOf(orarioLocal);
                            
                            OrarioTratta orario = new OrarioTratta();
                            orario.setAttivo(true);
                            orario.setGiorniSettimana(giorniStr);
                            orario.setOraPartenza(orarioTime);
                            orario.setTrattaId(trattaId);
                            
                            // Calcola l'ora di arrivo
                            int tempoTotaleMinuti = tratta.getFermataTrattaList().stream()
                                .mapToInt(ft -> ft.getTempoProssimaFermata())
                                .sum();
                            
                            LocalTime oraArrivo = orarioLocal.plusMinutes(tempoTotaleMinuti);
                            orario.setOraArrivo(Time.valueOf(oraArrivo));
                            
                            OrarioTrattaDAO.create(orario);
                            
                        } catch (DateTimeParseException e) {
                            logger.log(Level.WARNING, "Formato orario non valido: " + orarioStr, e);
                            // Continua con gli altri orari
                        } catch (SQLException e) {
                            logger.log(Level.SEVERE, "Errore nell'inserimento dell'orario", e);
                            // Continua con gli altri orari
                        }
                    }
                }
            }
            
            req.setAttribute("messaggio", "Tratta modificata con successo");
            
            // Redirect per evitare re-submit
            resp.sendRedirect(req.getContextPath() + "/prvAzienda/editTratta?id=" + trattaId + "&success=1");
            
        } catch (NumberFormatException e) {
            req.setAttribute("errore", "ID tratta non valido");
            doGet(req, resp);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'aggiornamento della tratta", e);
            req.setAttribute("errore", "Errore nell'aggiornamento: " + e.getMessage());
            doGet(req, resp);
        }
    }
    
    private String parseGiorni(String[] giorni) {
        if (giorni == null || giorni.length == 0) {
            return "";
        }
        return String.join(",", giorni);
    }
}
