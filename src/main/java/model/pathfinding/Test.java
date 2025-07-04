package model.pathfinding;

import model.sdata.*;
import model.udata.Azienda;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Test {

    public static void main(String[] args) {/*
        System.out.println("Avvio del test per l'algoritmo di PathFinding...");

        // --- 1. SETUP: Creazione dei dati di mock (Fermate, Tratte, Orari) ---
        Azienda azienda = new Azienda(1L, "OmniBus", "Urbana");

        Fermata casa = new Fermata(1L, "Casa", "Via Roma 1", new Coordinate(45.4642, 9.1900));
        Fermata stazione = new Fermata(2L, "Stazione Centrale", "Piazza Duca d'Aosta 1", new Coordinate(45.4862, 9.2040));
        Fermata ufficio = new Fermata(3L, "Ufficio", "Corso Como 15", new Coordinate(45.4815, 9.1883));
        Fermata museo = new Fermata(4L, "Museo Scienza", "Via San Vittore 21", new Coordinate(45.4627, 9.1604));

        // --- Creazione della LINEA 1 (Casa -> Stazione -> Museo) ---
        Tratta linea1 = new Tratta();
        linea1.setId(10L);
        linea1.setNome("Linea 1 - Circolare");
        linea1.setAzienda(azienda);
        List<FermataTratta> fermateLinea1 = new ArrayList<>();
        fermateLinea1.add(new FermataTratta(10L, casa, stazione, 15));
        fermateLinea1.add(new FermataTratta(10L, stazione, museo, 20));
        fermateLinea1.add(new FermataTratta(10L, museo, null, 0));
        linea1.setFermataTrattaList(fermateLinea1);
        List<UnicaTratta> corseLinea1 = new ArrayList<>();
        OrarioTratta orarioCorsa101 = new OrarioTratta();
        orarioCorsa101.setOraInizio(LocalTime.of(8, 0));
        orarioCorsa101.setGiorniSettimana(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)));
        corseLinea1.add(new UnicaTratta(101L, 10L, orarioCorsa101));
        linea1.setUnicaTrattaList(corseLinea1);

        // --- Creazione della LINEA 3 (Stazione -> Ufficio) ---
        Tratta linea3 = new Tratta();
        linea3.setId(30L);
        linea3.setNome("Linea 3 - Navetta Stazione-Ufficio");
        linea3.setAzienda(azienda);
        List<FermataTratta> fermateLinea3 = new ArrayList<>();
        fermateLinea3.add(new FermataTratta(30L, stazione, ufficio, 12));
        fermateLinea3.add(new FermataTratta(30L, ufficio, null, 0));
        linea3.setFermataTrattaList(fermateLinea3);
        List<UnicaTratta> corseLinea3 = new ArrayList<>();
        // Corsa che si perde perché parte troppo presto
        OrarioTratta orarioCorsa301 = new OrarioTratta();
        orarioCorsa301.setOraInizio(LocalTime.of(8, 10));
        orarioCorsa301.setGiorniSettimana(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)));
        corseLinea3.add(new UnicaTratta(301L, 30L, orarioCorsa301));
        // Corsa corretta che l'utente prenderà dopo il cambio
        OrarioTratta orarioCorsa302 = new OrarioTratta();
        orarioCorsa302.setOraInizio(LocalTime.of(8, 25));
        orarioCorsa302.setGiorniSettimana(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)));
        corseLinea3.add(new UnicaTratta(302L, 30L, orarioCorsa302));
        linea3.setUnicaTrattaList(corseLinea3);

        // Lista di tutte le tratte del sistema per il test
        List<Tratta> tutteLeTratte = Arrays.asList(linea1, linea3);

        // --- 2. ESECUZIONE: Chiamata all'algoritmo di PathFinding ---
        System.out.println("\n--- SCENARIO DI TEST: Viaggio con cambio ---");
        Fermata partenza = casa;
        Fermata destinazione = ufficio;
        LocalTime orarioPartenza = LocalTime.of(8, 0);
        DayOfWeek giorno = DayOfWeek.MONDAY;

        System.out.println("Partenza da: " + partenza.getNome());
        System.out.println("Destinazione: " + destinazione.getNome());
        System.out.println("Orario di partenza desiderato: " + orarioPartenza);
        System.out.println("Giorno: " + giorno);
        System.out.println("------------------------------------------\n");

        PathFinding pathFinder = new PathFinding();
        Percorso risultato = pathFinder.trovaPercorso(partenza, destinazione, orarioPartenza, giorno, tutteLeTratte);

        // --- 3. VERIFICA: Controllo del risultato ---
        if (risultato == null) {
            System.err.println("RISULTATO: TEST FALLITO - Nessun percorso trovato!");
            return;
        }

        System.out.println("Percorso trovato! Ecco i dettagli:");
        System.out.println("=============================================");
        System.out.println("Fermata iniziale: " + risultato.getFermataIniziale().getNome());
        System.out.println("Fermata finale: " + risultato.getFermataFinale().getNome());
        System.out.println("Orario di partenza: " + risultato.getOrarioPartenzaComplessivo());
        System.out.println("Orario di arrivo: " + risultato.getOrarioArrivoComplessivo());
        System.out.println("Durata totale: " + risultato.getDurataTotale().toMinutes() + " minuti");
        System.out.println("Numero di cambi: " + risultato.getNumeroCambi());
        System.out.println("---------------------------------------------");

        for (int i = 0; i < risultato.getTappe().size(); i++) {
            Percorso.Tappa tappa = risultato.getTappe().get(i);
            System.out.println("Tappa " + (i + 1) + ":");
            System.out.println("  - Prendi la " + tappa.getTratta().getNome());
            System.out.println("  - Parti da: " + tappa.getFermataPartenza().getNome() + " alle " + tappa.getOrarioPartenza());
            System.out.println("  - Arriva a: " + tappa.getFermataArrivo().getNome() + " alle " + tappa.getOrarioArrivo());
            if (i < risultato.getTappe().size() - 1) {
                System.out.println("  - CAMBIO ALLA FERMATA: " + tappa.getFermataArrivo().getNome());
            }
        }
        System.out.println("=============================================");

        // VERIFICA FINALE AUTOMATICA
        // Logica attesa:
        // 1. Parte da Casa alle 8:00 (Linea 1).
        // 2. Arriva a Stazione alle 8:15 (8:00 + 15 min).
        // 3. Perde la corsa delle 8:10 (Linea 3), attende quella delle 8:25.
        // 4. Parte da Stazione alle 8:25 (Linea 3).
        // 5. Arriva a Ufficio alle 8:37 (8:25 + 12 min).
        boolean testPassato = true;
        LocalTime expectedArrival = LocalTime.of(8, 37);
        if (!risultato.getOrarioArrivoComplessivo().equals(expectedArrival)) {
            System.err.println("ERRORE: Orario di arrivo non corretto. Atteso: " + expectedArrival + ", Trovato: " + risultato.getOrarioArrivoComplessivo());
            testPassato = false;
        }
        if (risultato.getNumeroCambi() != 1) {
            System.err.println("ERRORE: Numero di cambi non corretto. Atteso: 1, Trovato: " + risultato.getNumeroCambi());
            testPassato = false;
        }

        if (testPassato) {
            System.out.println("\nRISULTATO: ---> TEST PASSATO CON SUCCESSO! <---");
        } else {
            System.out.println("\nRISULTATO: ---> TEST FALLITO. Controllare gli errori sopra. <---");
        }
    */}
}