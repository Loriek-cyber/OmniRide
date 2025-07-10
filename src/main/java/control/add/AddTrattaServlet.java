package control.add;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dao.*;
import model.sdata.*;
import model.udata.Azienda;
import model.udata.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@WebServlet(name = "addTratta", value = "/prvAzienda/addTratta")
public class AddTrattaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("fermate", FermataDAO.getAll());
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle fermate", e);
        }
        req.getRequestDispatcher("/prvAzienda/addTratta.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utente non autenticato.");
            return;
        }

        String nome = req.getParameter("nome");
        String costoStr = req.getParameter("costo");
        String fermateStr = req.getParameter("fermateSelezionate");
        String tempiStr = req.getParameter("tempiTraFermate");
        String tempoTotaleStr = req.getParameter("tempoTotale");
        String orariInizioStr = req.getParameter("orariInizio");
        if (nome == null || nome.trim().isEmpty()) {
            req.setAttribute("errore", "Nome non inserito.");
            doGet(req, resp);
            return;
        }

        double costo;
        try {
            costo = Double.parseDouble(costoStr);
            if (costo < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            req.setAttribute("errore", "Costo non valido.");
            doGet(req, resp);
            return;
        }

        if (fermateStr == null || tempiStr == null) {
            req.setAttribute("errore", "Fermate o tempi mancanti.");
            doGet(req, resp);
            return;
        }

        String[] fermateArr = fermateStr.split(",");
        String[] tempiArr = tempiStr.split(",");

        if (fermateArr.length <= 1) {
            req.setAttribute("errore", "Devi selezionare almeno due fermate.");
            doGet(req, resp);
            return;
        }

        if (tempiArr.length != fermateArr.length - 1) {
            req.setAttribute("errore", "Numero di tempi non coerente con le fermate.");
            doGet(req, resp);
            return;
        }

        List<Fermata> tutteFermate;
        try {
            tutteFermate = FermataDAO.getAll();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle fermate", e);
        }

        List<FermataTratta> fermataTrattaList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < fermateArr.length; i++) {
            Long idFermata = Long.parseLong(fermateArr[i]);
            Fermata fermata = tutteFermate.stream()
                    .filter(f -> f.getId().equals(idFermata))
                    .findFirst()
                    .orElse(null);

            if (fermata == null) {
                req.setAttribute("errore", "Fermata non valida.");
                doGet(req, resp);
                return;
            }

            FermataTratta ft = new FermataTratta();
            ft.setFermata(fermata);
            ft.setSequenza(i + 1);

            if (i > 0) {
                    try {
                        int tempoInMinuti = Integer.parseInt(tempiArr[i - 1]);
                        ft.setTempoProssimaFermata(tempoInMinuti);
                    } catch (NumberFormatException e) {
                        req.setAttribute("errore", "Tempo non valido.");
                        doGet(req, resp);
                        return;
                    }
            }

            fermataTrattaList.add(ft);
        }



        // Costruzione Tratta
        Tratta tratta = new Tratta();
        tratta.setNome(nome);
        tratta.setAttiva(true);
        tratta.setCosto(costo);
        tratta.setFermataTrattaList(fermataTrattaList);

        // Parse del tempo totale
        int tempoTotaleMinuti = 0;
        if (tempoTotaleStr != null && !tempoTotaleStr.trim().isEmpty()) {
            try {
                tempoTotaleMinuti = Integer.parseInt(tempoTotaleStr);
            } catch (NumberFormatException e) {
                req.setAttribute("errore", "Tempo totale non valido.");
                doGet(req, resp);
                return;
            }
        }

        try {
            tratta.setOrari(parseOrariTratta(req, tempoTotaleMinuti));
        } catch (SQLException e) {
            req.setAttribute("errore", "Errore nella gestione degli orari: " + e.getMessage());
            doGet(req, resp);
            return;
        }



        try {
            Azienda azienda = AziendaDAO.fromIDutente(utente.getId());
            if (azienda == null) {
                throw new ServletException("Azienda non trovata per l'utente.");
            }
            tratta.setAzienda(azienda);
        } catch (SQLException e) {
            throw new ServletException("Errore nel recupero dell'azienda dell'utente.", e);
        }


        try {
            // Salva la tratta e ottieni l'ID
            Long trattaId = TrattaDAO.create(tratta);
        } catch (SQLException e) {
            req.setAttribute("errore", "Errore durante il salvataggio della tratta: " + e.getMessage());
            doGet(req, resp);
            return;
        }

        // Reindirizzamento a lista tratte
        resp.sendRedirect(req.getContextPath() + "/prvAzienda/tratte.jsp?id_tratta="+tratta.getId());
    }



    /**
     * Gestisce e salva gli orari della tratta
     */
    private List<OrarioTratta> parseOrariTratta(HttpServletRequest req, int tempoTotaleMinuti) throws SQLException {
        // Recupera gli orari dal JavaScript (formato: "HH:mm,HH:mm,HH:mm")
        String orariInizioStr = req.getParameter("orariInizio");

        // Recupera i giorni selezionati
        String[] giorniSelezionati = req.getParameterValues("giorni");

        if (orariInizioStr == null || orariInizioStr.trim().isEmpty() ||
            giorniSelezionati == null || giorniSelezionati.length == 0) {
            throw new SQLException("Orari e giorni sono obbligatori per creare una tratta");
        }

        // Parse degli orari
        String[] orariArray = orariInizioStr.split(",");
        List<LocalTime> orariParsed = new ArrayList<>();

        for (String orarioStr : orariArray) {
            orarioStr = orarioStr.trim();
            if (!orarioStr.isEmpty()) {
                try {
                    LocalTime orario = LocalTime.parse(orarioStr);
                    orariParsed.add(orario);
                } catch (DateTimeParseException e) {
                    throw new SQLException("Formato orario non valido: " + orarioStr);
                }
            }
        }

        if (orariParsed.isEmpty()) {
            throw new SQLException("Almeno un orario è necessario per creare la tratta");
        }

        // Converti i giorni in formato stringa per OrarioTratta
        String giorniString = convertGiorniToString(giorniSelezionati);

        // Calcola il tempo totale della tratta per determinare l'orario di arrivo

        // Crea un OrarioTratta per ogni orario di partenza
        List<OrarioTratta> listorarioTratta = new ArrayList<>();
        for (LocalTime oraPartenza : orariParsed) {
            OrarioTratta orarioTratta = new OrarioTratta();
            orarioTratta.setOraPartenza(Time.valueOf(oraPartenza));
            // Calcola l'orario di arrivo
            LocalTime oraArrivo = oraPartenza.plusMinutes(tempoTotaleMinuti);
            orarioTratta.setOraArrivo(Time.valueOf(oraArrivo));

            orarioTratta.setGiorniSettimana(giorniString);
            orarioTratta.setTipoServizio(OrarioTratta.TipoServizio.NORMALE);
            orarioTratta.setAttivo(true);
            orarioTratta.setNote("Orario creato automaticamente");
            listorarioTratta.add(orarioTratta);
        }

        return listorarioTratta;

    }

    /**
     * Converte i giorni selezionati nel formato stringa richiesto da OrarioTratta
     */
    private String convertGiorniToString(String[] giorniSelezionati) {
        StringBuilder giorni = new StringBuilder();
        for (int i = 0; i < giorniSelezionati.length; i++) {
            giorni.append(giorniSelezionati[i]);
            if (i < giorniSelezionati.length - 1) {
                giorni.append(",");
            }
        }
        return giorni.toString();
    }

    /**
     * Calcola il tempo totale di percorrenza di una tratta
     */
    private int calcolaTempoTotaleTratta(Long trattaId) throws SQLException {
        return TrattaDAO.calcolaTempoTotalePercorrenza(trattaId);
    }
}
