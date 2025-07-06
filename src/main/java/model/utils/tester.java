package model.utils;

import model.dao.FermataTrattaDAO;
import model.dao.OrarioTrattaDAO;
import model.dao.TrattaDAO;
import model.sdata.FermataTratta;
import model.sdata.OrarioTratta;
import model.sdata.Tratta;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class tester {
    public static void main(String[] args) {
        try {
            List<Tratta> tutteLeTratte = TrattaDAO.getAllTratte();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            if (tutteLeTratte.isEmpty()) {
                System.out.println("Nessuna tratta trovata nel database.");
                return;
            }

            for (Tratta tratta : tutteLeTratte) {
                System.out.println("======================================================");
                System.out.println("Tratta: " + tratta.getNome() + " (ID: " + tratta.getId() + ")");
                System.out.println("======================================================");

                // Carica le fermate e gli orari per la tratta corrente
                List<FermataTratta> fermateDellaTratta = FermataTrattaDAO.getFTfromTrattaID(tratta.getId());
                List<OrarioTratta> orariDellaTratta = OrarioTrattaDAO.getOrariByTrattaId(tratta.getId());

                if (orariDellaTratta.isEmpty()) {
                    System.out.println(" -> Nessun orario associato a questa tratta.");
                } else if (fermateDellaTratta.isEmpty()) {
                    System.out.println(" -> Nessuna fermata associata a questa tratta.");
                } else {
                    // Per ogni orario di partenza, simula il percorso
                    for (OrarioTratta orario : orariDellaTratta) {
                        LocalTime orarioCorrente = orario.getOraPartenza();
                        if (orarioCorrente == null) continue; // Salta se non c'è un'ora di partenza

                        System.out.println(" --- Percorso con partenza alle " + orarioCorrente.format(formatter) +
                                " (" + orario.getGiorniSettimana() + ") ---");

                        // Itera su ogni fermata per calcolare e stampare l'orario di passaggio
                        for (FermataTratta ft : fermateDellaTratta) {
                            System.out.println("  • " + ft.getFermata().getNome() +
                                    " - Arrivo previsto: " + orarioCorrente.format(formatter));

                            // Aggiorna l'orario per la fermata successiva
                            orarioCorrente = orarioCorrente.plusMinutes(ft.getTempoProssimaFermata());
                        }
                        // L'ultima fermata non ha un tempo_prossima_fermata, quindi l'ultimo orario calcolato
                        // è l'orario di arrivo al capolinea.
                        System.out.println("  • " + "Arrivo al capolinea" +
                                " - Orario previsto: " + orarioCorrente.format(formatter));
                    }
                }
                System.out.println(" ____________________________________________________"); // Spazio tra le tratte
            }

        } catch (SQLException e) {
            System.err.println("Si è verificato un errore durante l'accesso al database.");
            e.printStackTrace();
        }
    }
}


