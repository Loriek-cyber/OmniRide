package control.search;
import model.sdata.Tratta;
import model.sdata.FermataTratta;
import model.sdata.Fermata;
import model.dao.TrattaDAO;
import model.dao.FermataDAO;
import model.pathfinding.Percorso;
import model.pathfinding.Pathfinder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.sql.SQLException;

@WebServlet("/cercaTratte")
public class CercaTratteServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Recupera i parametri dal form
        String partenza = request.getParameter("partenza");
        String arrivo = request.getParameter("arrivo");
        String orarioStr = request.getParameter("orario");
        String dataStr = request.getParameter("data");
        String prezzoMaxStr = request.getParameter("prezzoMax");
        String durataMaxStr = request.getParameter("durataMax");
        String ordinamento = request.getParameter("ordinamento");
        
        // Validazione parametri obbligatori
        if (partenza == null || partenza.trim().isEmpty() || 
            arrivo == null || arrivo.trim().isEmpty() || 
            orarioStr == null || orarioStr.trim().isEmpty() || 
            dataStr == null || dataStr.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Tutti i campi di ricerca sono obbligatori.");
            request.getRequestDispatcher("/").forward(request, response);
            return;
        }
        
        try {
            // Parse dei parametri temporali
            LocalTime orario = LocalTime.parse(orarioStr);
            LocalDate data = LocalDate.parse(dataStr);
            
            // Recupera le tratte dal context dell'applicazione
            @SuppressWarnings("unchecked")
            List<Tratta> tutteLeTratte = (List<Tratta>) request.getServletContext().getAttribute("tratte");
            
            // Se non ci sono tratte nel context, caricale dal database
            if (tutteLeTratte == null) {
                tutteLeTratte = TrattaDAO.getAll();
            }
            
            // Prima cerca tratte dirette
            List<Tratta> tratteDirette = TrattaDAO.findByPartenzaArrivoDataOrario(
                partenza.trim(), arrivo.trim(), data, orario
            );
            
            List<Tratta> tratte = new ArrayList<>(tratteDirette);
            
            // Se non ci sono tratte dirette, usa il pathfinding per trovare percorsi complessi
            boolean usaPathfinding = tratte.isEmpty();
            
            // Applica filtri opzionali
            if (prezzoMaxStr != null && !prezzoMaxStr.isEmpty()) {
                double prezzoMax = Double.parseDouble(prezzoMaxStr);
                tratte = tratte.stream()
                    .filter(t -> t.getCosto() <= prezzoMax)
                    .collect(Collectors.toList());
            }
            
            if (durataMaxStr != null && !durataMaxStr.isEmpty()) {
                double durataMax = Double.parseDouble(durataMaxStr);
                tratte = tratte.stream()
                    .filter(t -> {
                        // Calcola la durata in ore
double durata = calcolaDurataInOre(t);
                        return durata <= durataMax;
                    })
                    .collect(Collectors.toList());
            }
            
            // Applica ordinamento
            if (ordinamento != null) {
                switch (ordinamento) {
                    case "prezzo":
                        tratte.sort((a, b) -> Double.compare(a.getCosto(), b.getCosto()));
                        break;
                    case "durata":
                        tratte.sort((a, b) -> {
                            double durataA = calcolaDurataInOre(a);
                            double durataB = calcolaDurataInOre(b);
                            return Double.compare(durataA, durataB);
                        });
                        break;
                    case "partenza":
                        tratte.sort((a, b) -> {
                            LocalTime timeA = a.getOrarioPartenza();
                            LocalTime timeB = b.getOrarioPartenza();
                            if (timeA == null && timeB == null) return 0;
                            if (timeA == null) return 1;
                            if (timeB == null) return -1;
                            return timeA.compareTo(timeB);
                        });
                        break;
                }
            }
            
// Converti tratte in percorsi
            List<Percorso> percorsi = new ArrayList<>();
            
            try {
                // Recupera le fermate dal database
                Fermata fermataPartenza = FermataDAO.ricercaPerNomeEsatto(partenza);
                Fermata fermataArrivo = FermataDAO.ricercaPerNomeEsatto(arrivo);
                
                // Se non troviamo le fermate esatte, cerca in tutte le tratte
                if (fermataPartenza == null || fermataArrivo == null) {
                    for (Tratta t : tutteLeTratte) {
                        for (FermataTratta ft : t.getFermataTrattaList()) {
                            if (fermataPartenza == null && ft.getFermata().getNome().equalsIgnoreCase(partenza)) {
                                fermataPartenza = ft.getFermata();
                            }
                            if (fermataArrivo == null && ft.getFermata().getNome().equalsIgnoreCase(arrivo)) {
                                fermataArrivo = ft.getFermata();
                            }
                        }
                    }
                }
                
                if (fermataPartenza != null && fermataArrivo != null) {
                    // Se ci sono tratte dirette, convertile in percorsi
                    if (!tratte.isEmpty()) {
                        for (Tratta tratta : tratte) {
                            Percorso percorso = new Percorso(tratta, fermataPartenza, fermataArrivo);
                            percorsi.add(percorso);
                        }
                    }
                    
                    // Se non ci sono tratte dirette o vogliamo anche percorsi complessi, usa il pathfinding
                    if (usaPathfinding || tratte.isEmpty()) {
                        System.out.println("[CercaTratteServlet] Utilizzando pathfinding per trovare percorsi complessi");
                        
                        // Trova il percorso ottimale con pathfinding
                        Percorso percorsoOttimale = Pathfinder.find(fermataPartenza, fermataArrivo, tutteLeTratte, orario);
                        if (percorsoOttimale != null) {
                            percorsi.add(percorsoOttimale);
                            System.out.println("[CercaTratteServlet] Trovato percorso ottimale con " + percorsoOttimale.getSegmenti().size() + " segmenti");
                        } else {
                            System.out.println("[CercaTratteServlet] Nessun percorso trovato con pathfinding");
                        }
                        
                        // Cerca anche altri percorsi alternativi (se implementato)
                        // Potresti implementare una ricerca di percorsi alternativi qui
                    }
                } else {
                    System.out.println("[CercaTratteServlet] Fermate non trovate: partenza=" + fermataPartenza + ", arrivo=" + fermataArrivo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[CercaTratteServlet] Errore durante la ricerca percorsi: " + e.getMessage());
            }

            // Passa i risultati alla JSP
            request.setAttribute("percorsi", percorsi);
            
            // Forward alla pagina dei risultati
            request.getRequestDispatcher("/risultati-ricerca.jsp").forward(request, response);
            
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Errore nel formato di data o ora
            request.setAttribute("errorMessage", "Formato data o ora non valido. Usa il formato corretto.");
            request.getRequestDispatcher("/").forward(request, response);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Errore nel formato numerico
            request.setAttribute("errorMessage", "Formato numerico non valido per prezzo o durata.");
            request.getRequestDispatcher("/").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            // Log dettagliato dell'errore
            System.err.println("[CercaTratteServlet] Errore durante la ricerca:");
            System.err.println("Partenza: " + partenza);
            System.err.println("Arrivo: " + arrivo);
            System.err.println("Data: " + dataStr);
            System.err.println("Orario: " + orarioStr);
            System.err.println("Errore: " + e.getMessage());
            
            // In caso di errore generico, mostra un messaggio più dettagliato
            request.setAttribute("errorMessage", "Si è verificato un errore durante la ricerca. Riprova più tardi.");
            request.setAttribute("errorDetails", e.getMessage());
            request.getRequestDispatcher("/").forward(request, response);
        }
    }
    
    private double calcolaDurataInOre(Tratta tratta) {
        if (tratta == null || tratta.getFermataTrattaList() == null || tratta.getFermataTrattaList().isEmpty()) {
            return 0.0;
        }
        // Somma tutti i tempi di percorrenza tra le fermate (esclusa l'ultima che non ha prossima fermata)
        int tempoTotaleMinuti = 0;
        List<FermataTratta> fermate = tratta.getFermataTrattaList();
        for (int i = 0; i < fermate.size() - 1; i++) {
            FermataTratta fermata = fermate.get(i);
            if (fermata != null) {
                tempoTotaleMinuti += fermata.getTempoProssimaFermata();
            }
        }
        return tempoTotaleMinuti / 60.0;
    }
}
