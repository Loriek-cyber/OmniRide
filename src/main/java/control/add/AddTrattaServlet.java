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
import java.util.stream.Collectors;

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
        String orariInizioParam = req.getParameter("orariInizio");
        String[] orariInizioStr = null;
        
        // Gestisce sia il parametro singolo che multiplo
        if (orariInizioParam != null && !orariInizioParam.trim().isEmpty()) {
            orariInizioStr = orariInizioParam.split(",");
        } else {
            String[] orariParams = req.getParameterValues("orariInizio");
            if (orariParams != null) {
                orariInizioStr = orariParams;
            }
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



        // Gestione degli orari
        List<OrarioTratta> orariTratta = new ArrayList<>();
        String[] giorni = req.getParameterValues("giorni");
        
        if (orariInizioStr != null && orariInizioStr.length > 0) {
            String giorniStr = (giorni != null) ? parseGiorni(giorni) : "";
            
            // Usa un Set per rimuovere automaticamente i duplicati
            Set<String> orariUnici = new LinkedHashSet<>(Arrays.asList(orariInizioStr));
            
            for (String orarioStr : orariUnici) {
                if (orarioStr != null && !orarioStr.trim().isEmpty()) {
                    try {
                        // Rimuovi eventuali spazi bianchi
                        orarioStr = orarioStr.trim();
                        
                        // Verifica il formato dell'orario
                        LocalTime orarioLocal = LocalTime.parse(orarioStr);
                        Time orarioTime = Time.valueOf(orarioLocal);
                        
                        OrarioTratta or = new OrarioTratta();
                        or.setAttivo(true);
                        or.setGiorniSettimana(giorniStr);
                        or.setOraPartenza(orarioTime);
                        orariTratta.add(or);
                    } catch (DateTimeParseException e) {
                        req.setAttribute("errore", "Formato orario non valido: " + orarioStr + ". Usa il formato HH:mm");
                        doGet(req, resp);
                        return;
                    } catch (Exception e) {
                        req.setAttribute("errore", "Errore nella gestione dell'orario: " + orarioStr);
                        doGet(req, resp);
                        return;
                    }
                }
            }
        }
        
        if (orariTratta.isEmpty()) {
            req.setAttribute("errore", "Inserisci almeno un orario di partenza.");
            doGet(req, resp);
            return;
        }

        // Costruzione Tratta
        Tratta tratta = new Tratta();
        tratta.setNome(nome);
        tratta.setAttiva(true);
        tratta.setCosto(costo);
        tratta.setFermataTrattaList(fermataTrattaList);
        tratta.setOrari(orariTratta);




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
            tratta.validator();
        } catch (Exception e) {
            req.setAttribute("errore", "Errore nella validazione della tratta: " + e.getMessage());
            doGet(req, resp);
            return;
        }

        // Debug degli orari (con controllo null)
        System.out.println("=== DEBUG ORARI TRATTA ===");
        if (tratta.getOrari() != null) {
            tratta.getOrari().forEach(orario -> {
                System.out.println("Orario partenza: " + orario.getOraPartenza());
                System.out.println("Giorni: " + orario.getGiorniSettimana());
                if (orario.getListatime() != null) {
                    orario.getListatime().forEach(tempo -> {
                        System.out.println("Tempo: " + tempo.toString());
                    });
                } else {
                    System.out.println("Lista tempi: null");
                }
                System.out.println("-----------------------");
            });
        } else {
            System.out.println("Nessun orario configurato");
        }
        System.out.println("=== FINE DEBUG ORARI ===");
        Long trattaId;
        try {
            // Salva la tratta e ottieni l'ID
            trattaId = TrattaDAO.create(tratta);
        } catch (SQLException e) {
            req.setAttribute("errore", "Errore durante il salvataggio della tratta: " + e.getMessage());
            doGet(req, resp);
            return;
        }

        // Reindirizzamento a lista tratte
        resp.sendRedirect(req.getContextPath() + "/prvAzienda/tratte.jsp?id_tratta="+trattaId);

    }



    private String parseGiorni(String[] giorni) {
        String re = "";
        for (int i = 0; i < giorni.length; i++) {
            if(i==0){
                re+=giorni[i];
            }
            else{
                re+=","+giorni[i];
            }
        }
        return re;
    }



}
