package model.dao;

import model.db.DBConnector;
import model.pathfinding.Percorso;
import model.pathfinding.SegmentoPercorso;
import model.sdata.Fermata;
import model.sdata.Tratta;
import model.udata.Biglietto;
import model.udata.Utente;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class per verificare l'acquisto di biglietti da parte di ospiti (guest users).
 * Questa classe testa la funzionalità di acquisto senza un codice utente registrato.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GuestTicketPurchaseTest {

    private static final Long GUEST_USER_ID = -1L; // ID speciale per gli ospiti
    private static Long testTrattaId;
    private static Long testBigliettoId;
    
    @BeforeAll
    static void setUp() throws SQLException {
        // Crea dati di test necessari
        setupTestData();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // Pulizia dei dati di test
        cleanupTestData();
    }

    /**
     * Test 1: Verifica che sia possibile creare un biglietto per un ospite
     */
    @Test
    @Order(1)
    @DisplayName("Test creazione biglietto ospite - Scenario base")
    void testCreateGuestTicket() {
        System.out.println("=== Test 1: Creazione biglietto ospite ===");
        
        try {
            // Crea un percorso di test
            Percorso percorso = createTestPercorso();
            
            // Crea un biglietto per ospite senza utente registrato
            Biglietto guestTicket = createGuestTicket(percorso);
            
            // Verifica che il biglietto sia stato creato
            assertNotNull(guestTicket);
            assertEquals(GUEST_USER_ID, guestTicket.getId_utente());
            assertEquals(Biglietto.StatoBiglietto.ACQUISTATO, guestTicket.getStato());
            assertTrue(guestTicket.getPrezzo() > 0);
            
            // Salva nel database
            testBigliettoId = BigliettiDAO.create(guestTicket);
            
            assertNotNull(testBigliettoId);
            System.out.println("✓ Biglietto ospite creato con ID: " + testBigliettoId);
            
        } catch (Exception e) {
            fail("Errore durante la creazione del biglietto ospite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test 2: Verifica il recupero di un biglietto ospite
     */
    @Test
    @Order(2)
    @DisplayName("Test recupero biglietto ospite")
    void testRetrieveGuestTicket() {
        System.out.println("=== Test 2: Recupero biglietto ospite ===");
        
        try {
            // Recupera il biglietto creato nel test precedente
            Biglietto retrievedTicket = BigliettiDAO.getbyId(testBigliettoId);
            
            assertNotNull(retrievedTicket);
            assertEquals(GUEST_USER_ID, retrievedTicket.getId_utente());
            assertEquals(testBigliettoId, retrievedTicket.getId());
            
            System.out.println("✓ Biglietto ospite recuperato: " + retrievedTicket.toString());
            
        } catch (Exception e) {
            fail("Errore durante il recupero del biglietto ospite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test 3: Verifica l'attivazione di un biglietto ospite
     */
    @Test
    @Order(3)
    @DisplayName("Test attivazione biglietto ospite")
    void testActivateGuestTicket() {
        System.out.println("=== Test 3: Attivazione biglietto ospite ===");
        
        try {
            // Recupera il biglietto
            Biglietto guestTicket = BigliettiDAO.getbyId(testBigliettoId);
            assertNotNull(guestTicket);
            
            // Attiva il biglietto
            boolean activated = guestTicket.attiva();
            assertTrue(activated);
            assertEquals(Biglietto.StatoBiglietto.CONVALIDATO, guestTicket.getStato());
            assertNotNull(guestTicket.getDataConvalida());
            assertNotNull(guestTicket.getDataFine());
            
            // Aggiorna nel database
            BigliettiDAO.update(guestTicket);
            
            // Verifica che sia stato aggiornato
            Biglietto updatedTicket = BigliettiDAO.getbyId(testBigliettoId);
            assertEquals(Biglietto.StatoBiglietto.CONVALIDATO, updatedTicket.getStato());
            
            System.out.println("✓ Biglietto ospite attivato con successo");
            
        } catch (Exception e) {
            fail("Errore durante l'attivazione del biglietto ospite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test 4: Verifica che non sia possibile recuperare biglietti ospite con getAllUser
     */
    @Test
    @Order(4)
    @DisplayName("Test recupero biglietti per utente ospite")
    void testGetAllGuestTickets() {
        System.out.println("=== Test 4: Recupero biglietti utente ospite ===");
        
        try {
            // Tenta di recuperare tutti i biglietti per l'utente ospite
            List<Biglietto> guestTickets = BigliettiDAO.getAllUser(GUEST_USER_ID);
            
            // Dovrebbe contenere almeno il biglietto che abbiamo creato
            assertNotNull(guestTickets);
            assertTrue(guestTickets.size() >= 1);
            
            // Verifica che tutti i biglietti appartengano all'utente ospite
            for (Biglietto ticket : guestTickets) {
                assertEquals(GUEST_USER_ID, ticket.getId_utente());
            }
            
            System.out.println("✓ Trovati " + guestTickets.size() + " biglietti per utente ospite");
            
        } catch (Exception e) {
            fail("Errore durante il recupero dei biglietti ospite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test 5: Test con diversi tipi di biglietto per ospiti
     */
    @Test
    @Order(5)
    @DisplayName("Test biglietti ospite con diversi tipi")
    void testGuestTicketTypes() {
        System.out.println("=== Test 5: Biglietti ospite con diversi tipi ===");
        
        try {
            Percorso percorso = createTestPercorso();
            
            // Test biglietto normale
            Biglietto normalTicket = createGuestTicket(percorso);
            normalTicket.setTipo(Biglietto.TipoBiglietto.NORMALE);
            Long normalId = BigliettiDAO.create(normalTicket);
            assertNotNull(normalId);
            
            // Test biglietto giornaliero
            Biglietto dailyTicket = createGuestTicket(percorso);
            dailyTicket.setTipo(Biglietto.TipoBiglietto.GIORNALIERO);
            dailyTicket.setPrezzo(percorso.getCosto() * 3);
            Long dailyId = BigliettiDAO.create(dailyTicket);
            assertNotNull(dailyId);
            
            System.out.println("✓ Biglietti ospite creati: normale=" + normalId + ", giornaliero=" + dailyId);
            
            // Cleanup
            BigliettiDAO.delete(BigliettiDAO.getbyId(normalId));
            BigliettiDAO.delete(BigliettiDAO.getbyId(dailyId));
            
        } catch (Exception e) {
            fail("Errore durante il test dei tipi di biglietto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test 6: Test validazione biglietto ospite
     */
    @Test
    @Order(6)
    @DisplayName("Test validazione biglietto ospite")
    void testGuestTicketValidation() {
        System.out.println("=== Test 6: Validazione biglietto ospite ===");
        
        try {
            // Recupera il biglietto attivato
            Biglietto guestTicket = BigliettiDAO.getbyId(testBigliettoId);
            assertNotNull(guestTicket);
            
            // Verifica che sia valido
            boolean isValid = guestTicket.verifica();
            assertTrue(isValid);
            
            // Simula scadenza modificando manualmente la data fine
            guestTicket.setDataFine(LocalTime.now().minusHours(1));
            boolean isExpired = guestTicket.verifica();
            assertFalse(isExpired);
            assertEquals(Biglietto.StatoBiglietto.SCADUTO, guestTicket.getStato());
            
            System.out.println("✓ Validazione biglietto ospite funzionante");
            
        } catch (Exception e) {
            fail("Errore durante la validazione del biglietto ospite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test 7: Test stress - creazione multipla di biglietti ospite
     */
    @Test
    @Order(7)
    @DisplayName("Test stress - biglietti ospite multipli")
    void testMultipleGuestTickets() {
        System.out.println("=== Test 7: Creazione multipla biglietti ospite ===");
        
        List<Long> createdIds = new ArrayList<>();
        
        try {
            Percorso percorso = createTestPercorso();
            
            // Crea 10 biglietti ospite
            for (int i = 0; i < 10; i++) {
                Biglietto guestTicket = createGuestTicket(percorso);
                guestTicket.setPrezzo(percorso.getCosto() + i); // Prezzo diverso per distinguere
                
                Long ticketId = BigliettiDAO.create(guestTicket);
                assertNotNull(ticketId);
                createdIds.add(ticketId);
            }
            
            // Verifica che tutti siano stati creati
            assertEquals(10, createdIds.size());
            
            // Verifica che siano tutti diversi
            assertEquals(10, createdIds.stream().distinct().count());
            
            System.out.println("✓ Creati " + createdIds.size() + " biglietti ospite");
            
        } catch (Exception e) {
            fail("Errore durante il test stress: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cleanup
            for (Long id : createdIds) {
                try {
                    BigliettiDAO.delete(BigliettiDAO.getbyId(id));
                } catch (Exception e) {
                    System.err.println("Errore durante la pulizia del biglietto " + id + ": " + e.getMessage());
                }
            }
        }
    }

    // === METODI DI SUPPORTO ===

    /**
     * Crea un biglietto per un ospite (senza utente registrato)
     */
    private Biglietto createGuestTicket(Percorso percorso) {
        Biglietto guestTicket = new Biglietto();
        guestTicket.setId_utente(GUEST_USER_ID); // ID speciale per ospiti
        guestTicket.setDataAcquisto(LocalTime.now());
        guestTicket.setStato(Biglietto.StatoBiglietto.ACQUISTATO);
        guestTicket.setPrezzo(percorso.getCosto());
        guestTicket.setTipo(Biglietto.TipoBiglietto.NORMALE);
        
        // Imposta tratte dal percorso
        List<Long> idTratte = new ArrayList<>();
        List<Integer> numeroFermate = new ArrayList<>();
        
        for (SegmentoPercorso segmento : percorso.getSegmenti()) {
            idTratte.add(segmento.getId_tratta());
            numeroFermate.add(segmento.getNumero_fermate());
        }
        
        guestTicket.setId_tratte(idTratte);
        guestTicket.setNumero_fermate(numeroFermate);
        
        return guestTicket;
    }

    /**
     * Crea un percorso di test
     */
    private Percorso createTestPercorso() {
        SegmentoPercorso segmento = new SegmentoPercorso();
        segmento.setId_tratta(testTrattaId);
        segmento.setNumero_fermate(3);
        segmento.setTempo_partenza(LocalTime.now());
        segmento.setTempo_arrivo(LocalTime.now().plusMinutes(30));
        
        List<SegmentoPercorso> segmenti = new ArrayList<>();
        segmenti.add(segmento);
        
        return new Percorso(segmenti, 5.50); // Prezzo base di 5.50€
    }

    /**
     * Imposta i dati di test necessari
     */
    private static void setupTestData() throws SQLException {
        System.out.println("=== Setup dati di test ===");
        
        try {
            // Recupera una tratta esistente o crea una di test
            List<Tratta> tratte = TrattaDAO.getAll();
            if (!tratte.isEmpty()) {
                testTrattaId = tratte.get(0).getId();
                System.out.println("Usando tratta esistente: " + testTrattaId);
            } else {
                // Se non ci sono tratte, usa un ID di test
                testTrattaId = 1L;
                System.out.println("Usando tratta ID di test: " + testTrattaId);
            }
            
        } catch (Exception e) {
            System.err.println("Errore durante il setup: " + e.getMessage());
            testTrattaId = 1L; // Fallback
        }
    }

    /**
     * Pulizia dei dati di test
     */
    private static void cleanupTestData() throws SQLException {
        System.out.println("=== Cleanup dati di test ===");
        
        try {
            if (testBigliettoId != null) {
                Biglietto toDelete = BigliettiDAO.getbyId(testBigliettoId);
                if (toDelete != null) {
                    BigliettiDAO.delete(toDelete);
                    System.out.println("Biglietto test eliminato: " + testBigliettoId);
                }
            }
        } catch (Exception e) {
            System.err.println("Errore durante la pulizia: " + e.getMessage());
        }
    }
}
