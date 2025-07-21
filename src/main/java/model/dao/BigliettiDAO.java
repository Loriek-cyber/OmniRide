package model.dao;
import model.db.DBConnector;
import model.udata.Biglietto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BigliettiDAO {
    
    // Query SQL - Updated to match actual database schema
    private static final String INSERT_BIGLIETTO = 
        "INSERT INTO Biglietto (id_utente, nome, data_acquisto, data_convalida, data_scadenza, stato, prezzo_pagato, tipo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String INSERT_BIGLIETTO_TRATTA = 
        "INSERT INTO Biglietto_Tratta (id_biglietto, id_tratta, fermate_percorse) VALUES (?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT * FROM Biglietto WHERE id = ?";
    
    private static final String SELECT_TRATTE_BY_BIGLIETTO = 
        "SELECT * FROM Biglietto_Tratta WHERE id_biglietto = ?";
    
    private static final String SELECT_ALL = 
        "SELECT * FROM Biglietto";
    
    private static final String SELECT_BY_USER = 
        "SELECT * FROM Biglietto WHERE id_utente = ?";
    
    private static final String SELECT_BY_USER_ACTIVE = 
        "SELECT * FROM Biglietto WHERE id_utente = ? AND stato IN ('ACQUISTATO', 'CONVALIDATO')";
    
    private static final String SELECT_BY_USER_INACTIVE = 
        "SELECT * FROM Biglietto WHERE id_utente = ? AND stato IN ('SCADUTO', 'ANNULLATO')";
    
    private static final String SELECT_GUEST_TICKETS = 
        "SELECT * FROM Biglietto WHERE id_utente = -1";
    
    private static final String UPDATE_BIGLIETTO = 
        "UPDATE Biglietto SET id_utente = ?, nome = ?, data_acquisto = ?, data_convalida = ?, data_scadenza = ?, stato = ?, prezzo_pagato = ?, tipo = ? WHERE id = ?";
    
    private static final String DELETE_BIGLIETTO = 
        "DELETE FROM Biglietto WHERE id = ?";
    
    private static final String DELETE_BIGLIETTO_TRATTA = 
        "DELETE FROM Biglietto_Tratta WHERE id_biglietto = ?";
    private static final String SELECT_BY_TYPE = "Select * from Biglietto where tipo = ?";
    private static final String SELECT_TOTAL_REVENUE_BY_AZIENDA = "SELECT SUM(b.prezzo_pagato) FROM Biglietto b JOIN Tratta t ON b.id_tratta = t.id WHERE t.id_azienda = ? AND b.stato IN ('ACQUISTATO', 'CONVALIDATO')";
    private static final String SELECT_TOTAL_TICKETS_SOLD_BY_AZIENDA = "SELECT COUNT(b.id) FROM Biglietto b JOIN Tratta t ON b.id_tratta = t.id WHERE t.id_azienda = ? AND b.stato IN ('ACQUISTATO', 'CONVALIDATO')";
    private static final String SELECT_BY_USER_AND_TYPE = "SELECT * from Biglietto where id_utente = ? AND tipo = ?";
    
    private static final String UPDATE_EXPIRED_TICKETS = 
        "UPDATE Biglietto SET stato = 'SCADUTO' WHERE stato = 'CONVALIDATO' AND data_scadenza < NOW()";

    /**
     * Calcola il totale dei ricavi per un'azienda.
     * @param idAzienda ID dell'azienda
     * @return Totale ricavi
     * @throws SQLException in caso di errore del database
     */
    public static double getTotalRevenueByAzienda(Long idAzienda) throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_TOTAL_REVENUE_BY_AZIENDA)) {
            stmt.setLong(1, idAzienda);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    public static int getTotalTicketsSoldByAzienda(Long idAzienda) throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_TOTAL_TICKETS_SOLD_BY_AZIENDA)) {
            stmt.setLong(1, idAzienda);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }


    //funzioni basilari
    public static void delete(Biglietto biglietto) {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);
            
            // Prima elimina i collegamenti nella tabella Biglietto_Tratta
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_BIGLIETTO_TRATTA)) {
                stmt.setLong(1, biglietto.getId());
                stmt.executeUpdate();
            }
            
            // Poi elimina il biglietto
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_BIGLIETTO)) {
                stmt.setLong(1, biglietto.getId());
                stmt.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'eliminazione del biglietto", e);
        }
    }

    public static void update(Biglietto biglietto) {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);
            
            // Aggiorna il biglietto principale
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_BIGLIETTO)) {
                stmt.setLong(1, biglietto.getId_utente());
                stmt.setString(2, biglietto.getNome());
                stmt.setTimestamp(3, biglietto.getDataAcquisto() != null ? Timestamp.valueOf(biglietto.getDataAcquisto().atDate(java.time.LocalDate.now())) : null);
                stmt.setTimestamp(4, biglietto.getDataConvalida() != null ? Timestamp.valueOf(biglietto.getDataConvalida().atDate(java.time.LocalDate.now())) : null);
                stmt.setTimestamp(5, biglietto.getDataFine() != null ? Timestamp.valueOf(biglietto.getDataFine().atDate(java.time.LocalDate.now())) : null);
                stmt.setString(6, biglietto.getStato().name());
                stmt.setDouble(7, biglietto.getPrezzo());
                stmt.setString(8, biglietto.getTipo() != null ? biglietto.getTipo().name() : "NORMALE");
                stmt.setLong(9, biglietto.getId());
                stmt.executeUpdate();
            }
            
            // Elimina i collegamenti esistenti
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_BIGLIETTO_TRATTA)) {
                stmt.setLong(1, biglietto.getId());
                stmt.executeUpdate();
            }
            
            // Inserisce i nuovi collegamenti
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_BIGLIETTO_TRATTA)) {
                for (int i = 0; i < biglietto.getId_tratte().size(); i++) {
                    stmt.setLong(1, biglietto.getId());
                    stmt.setLong(2, biglietto.getId_tratte().get(i));
                    stmt.setInt(3, biglietto.getNumero_fermate().get(i));
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'aggiornamento del biglietto", e);
        }
    }

    /**
     * Ensures that the guest user (-1) exists in the database
     * @throws SQLException if database operation fails
     */
    private static void ensureGuestUserExists() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Check if guest user exists
            String checkQuery = "SELECT COUNT(*) FROM Utente WHERE id = -1";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        // Guest user doesn't exist, create it
                        String insertQuery = "INSERT INTO Utente (id, nome, cognome, email, password_hash, data_registrazione, ruolo) " +
                                           "VALUES (-1, 'Guest', 'User', 'guest@omniride.local', 'GUEST_USER_NO_PASSWORD', NOW(), 'utente')";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        }
    }
    
    public static Long create(Biglietto biglietto) {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);
            
            // Ensure guest user exists if creating a guest ticket
            if (biglietto.getId_utente() == -1L) {
                ensureGuestUserExists();
                System.out.println("[BIGLIETTI_DAO DEBUG] Creando biglietto guest per utente ID: -1");
            }
            
            Long bigliettoId = null;
            
            // Inserisce il biglietto principale
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_BIGLIETTO, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, biglietto.getId_utente());
                stmt.setString(2, biglietto.getNome());
                // Conversione da LocalTime a LocalDateTime usando la data corrente
                stmt.setTimestamp(3, biglietto.getDataAcquisto() != null ? 
                    Timestamp.valueOf(biglietto.getDataAcquisto().atDate(java.time.LocalDate.now())) : null);
                stmt.setTimestamp(4, biglietto.getDataConvalida() != null ? 
                    Timestamp.valueOf(biglietto.getDataConvalida().atDate(java.time.LocalDate.now())) : null);
                stmt.setTimestamp(5, biglietto.getDataFine() != null ? 
                    createDataFineTimestamp(biglietto.getDataFine()) : null);
                stmt.setString(6, biglietto.getStato().name());
                stmt.setDouble(7, biglietto.getPrezzo());
                stmt.setString(8, biglietto.getTipo() != null ? biglietto.getTipo().name() : "NORMALE");
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            bigliettoId = generatedKeys.getLong(1);
                            biglietto.setId(bigliettoId);
                        }
                    }
                }
            }
            
            // Inserisce i collegamenti nella tabella Biglietto_Tratta
            if (bigliettoId != null && !biglietto.getId_tratte().isEmpty()) {
                try (PreparedStatement stmt = conn.prepareStatement(INSERT_BIGLIETTO_TRATTA)) {
                    for (int i = 0; i < biglietto.getId_tratte().size(); i++) {
                        stmt.setLong(1, bigliettoId);
                        stmt.setLong(2, biglietto.getId_tratte().get(i));
                        stmt.setInt(3, biglietto.getNumero_fermate().get(i));
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }
            
            conn.commit();
            return bigliettoId;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la creazione del biglietto", e);
        }
    }

    public static Biglietto getbyId(Long id) {
        try (Connection conn = DBConnector.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
                stmt.setLong(1, id);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToBiglietto(rs, conn);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Biglietto> getAll() {
        List<Biglietto> biglietti = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        biglietti.add(mapResultSetToBiglietto(rs, conn));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return biglietti;
    }

    //funzioni specifiche
    public static List<Biglietto> getAllUser(Long userId) {
        List<Biglietto> biglietti = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER)) {
                stmt.setLong(1, userId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        biglietti.add(mapResultSetToBiglietto(rs, conn));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return biglietti;
    }

    public static List<Biglietto> getAllUserActive(Long userId) {
        List<Biglietto> biglietti = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            // Include sia i biglietti acquistati (inattivi) che quelli convalidati (attivi)
            // Per i biglietti acquistati, non verifichiamo la scadenza perché non sono ancora attivati
            String query = "SELECT * FROM Biglietto WHERE id_utente = ? AND " +
                          "(stato = 'ACQUISTATO' OR " +
                          "(stato = 'CONVALIDATO' AND (data_scadenza IS NULL OR data_scadenza > NOW()))) " +
                          "ORDER BY data_acquisto DESC";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, userId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        biglietti.add(mapResultSetToBiglietto(rs, conn));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return biglietti;
    }

    public static List<Biglietto> getAllUserInactive(Long userId) {
        List<Biglietto> biglietti = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER_INACTIVE)) {
                stmt.setLong(1, userId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        biglietti.add(mapResultSetToBiglietto(rs, conn));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return biglietti;
    }
    
    /**
     * Recupera tutti i biglietti di un tipo specifico
     * @param tipo Il tipo di biglietto da cercare
     * @return Lista di biglietti del tipo specificato
     */
    public static List<Biglietto> getAllByType(Biglietto.TipoBiglietto tipo) {
        List<Biglietto> biglietti = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TYPE)) {
                stmt.setString(1, tipo.name());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        biglietti.add(mapResultSetToBiglietto(rs, conn));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return biglietti;
    }
    
    /**
     * Recupera tutti i biglietti degli ospiti (utenti guest)
     * @return Lista di biglietti degli ospiti
     */
    public static List<Biglietto> getAllGuestTickets() {
        List<Biglietto> biglietti = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_GUEST_TICKETS)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        biglietti.add(mapResultSetToBiglietto(rs, conn));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return biglietti;
    }
    
    /**
     * Recupera tutti i biglietti di un utente di un tipo specifico
     * @param userId ID dell'utente
     * @param tipo Tipo di biglietto
     * @return Lista di biglietti dell'utente del tipo specificato
     */
    public static List<Biglietto> getAllByUserAndType(Long userId, Biglietto.TipoBiglietto tipo) {
        List<Biglietto> biglietti = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER_AND_TYPE)) {
                stmt.setLong(1, userId);
                stmt.setString(2, tipo.name());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        biglietti.add(mapResultSetToBiglietto(rs, conn));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return biglietti;
    }
    
    /**
     * Verifica se un biglietto è un biglietto ospite
     * @param bigliettoId ID del biglietto
     * @return true se è un biglietto ospite, false altrimenti
     */
    public static boolean isGuestTicket(Long bigliettoId) {
        Biglietto biglietto = getbyId(bigliettoId);
        return biglietto != null && biglietto.getId_utente() == -1L;
    }
    
    /**
     * Reclama i biglietti guest assegnandoli a un utente registrato
     * @param userId ID dell'utente che reclama i biglietti
     * @param guestTicketIds Lista degli ID dei biglietti guest da reclamare
     * @return Numero di biglietti reclamati con successo
     */
    public static int claimGuestTickets(Long userId, List<Long> guestTicketIds) {
        if (guestTicketIds == null || guestTicketIds.isEmpty()) {
            return 0;
        }
        
        int claimedCount = 0;
        
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);
            
            String updateQuery = "UPDATE Biglietto SET id_utente = ? WHERE id = ? AND id_utente = -1";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                for (Long ticketId : guestTicketIds) {
                    stmt.setLong(1, userId);
                    stmt.setLong(2, ticketId);
                    
                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        claimedCount++;
                    }
                }
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il reclamo dei biglietti guest", e);
        }
        
        return claimedCount;
    }
    
    /**
     * Reclama tutti i biglietti guest assegnandoli a un utente registrato
     * @param userId ID dell'utente che reclama i biglietti
     * @return Numero di biglietti reclamati con successo
     */
    public static int claimAllGuestTickets(Long userId) {
        try (Connection conn = DBConnector.getConnection()) {
            String updateQuery = "UPDATE Biglietto SET id_utente = ? WHERE id_utente = -1";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setLong(1, userId);
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il reclamo di tutti i biglietti guest", e);
        }
    }
    
    /**
     * Invalida automaticamente tutti i biglietti che hanno superato il tempo di scadenza.
     * Questo metodo aggiorna lo stato dei biglietti convalidati la cui data di scadenza
     * è precedente al momento attuale, impostandoli come "SCADUTO".
     * 
     * @return Il numero di biglietti invalidati
     */
    public static int invalidaBigliettiScaduti() {
        try (Connection conn = DBConnector.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_EXPIRED_TICKETS)) {
                int bigliettiInvalidati = stmt.executeUpdate();
                
                if (bigliettiInvalidati > 0) {
                    System.out.println("Invalidati " + bigliettiInvalidati + " biglietti scaduti.");
                }
                
                return bigliettiInvalidati;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'invalidamento dei biglietti scaduti", e);
        }
    }
    
    /**
     * Recupera i biglietti attivi (non scaduti) tramite una lista di ID
     * Utile per i biglietti guest salvati come ID nella sessione
     * 
     * @param ticketIds Lista degli ID dei biglietti da recuperare
     * @return Lista di biglietti attivi corrispondenti agli ID forniti
     */
    public static List<Biglietto> getActiveTicketsByIds(List<Long> ticketIds) {
        List<Biglietto> biglietti = new ArrayList<>();
        
        if (ticketIds == null || ticketIds.isEmpty()) {
            return biglietti;
        }
        
        // Prima invalida tutti i biglietti scaduti
        invalidaBigliettiScaduti();
        
        try (Connection conn = DBConnector.getConnection()) {
            // Costruisci la query con placeholder per ogni ID
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Biglietto WHERE id IN (");
            for (int i = 0; i < ticketIds.size(); i++) {
                if (i > 0) queryBuilder.append(", ");
                queryBuilder.append("?");
            }
            queryBuilder.append(") AND stato IN ('ACQUISTATO', 'CONVALIDATO')");
            
            try (PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
                // Imposta i parametri
                for (int i = 0; i < ticketIds.size(); i++) {
                    stmt.setLong(i + 1, ticketIds.get(i));
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Biglietto biglietto = mapResultSetToBiglietto(rs, conn);
                        
                        // Doppio controllo: verifica la scadenza in memoria
                        biglietto.verificaScadenza();
                        
                        // Aggiungi solo se non è scaduto dopo la verifica in memoria
                        if (biglietto.getStato() != Biglietto.StatoBiglietto.SCADUTO) {
                            biglietti.add(biglietto);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il recupero dei biglietti per ID", e);
        }
        
        return biglietti;
    }
    
    // Metodo di utilità per mappare ResultSet a Biglietto
    private static Biglietto mapResultSetToBiglietto(ResultSet rs, Connection conn) throws SQLException {
        Biglietto biglietto = new Biglietto();
        
        biglietto.setId(rs.getLong("id"));
        biglietto.setId_utente(rs.getLong("id_utente"));
        biglietto.setNome(rs.getString("nome"));
        biglietto.setPrezzo(rs.getDouble("prezzo_pagato"));
        biglietto.setStato(Biglietto.StatoBiglietto.valueOf(rs.getString("stato")));
        
        // Mappatura del campo tipo
        String tipoString = rs.getString("tipo");
        if (tipoString != null) {
            biglietto.setTipo(Biglietto.TipoBiglietto.valueOf(tipoString));
        } else {
            biglietto.setTipo(Biglietto.TipoBiglietto.NORMALE); // Default
        }
        
        // Conversione timestamp a LocalTime mantenendo la logica della data
        Timestamp dataAcquisto = rs.getTimestamp("data_acquisto");
        if (dataAcquisto != null) {
            LocalDateTime dataAcquistoDateTime = dataAcquisto.toLocalDateTime();
            biglietto.setDataAcquisto(dataAcquistoDateTime.toLocalTime());
        }
        
        Timestamp dataConvalida = rs.getTimestamp("data_convalida");
        if (dataConvalida != null) {
            LocalDateTime dataConvalidaDateTime = dataConvalida.toLocalDateTime();
            biglietto.setDataConvalida(dataConvalidaDateTime.toLocalTime());
        }
        
        Timestamp dataScadenza = rs.getTimestamp("data_scadenza");
        if (dataScadenza != null) {
            LocalDateTime dataScadenzaDateTime = dataScadenza.toLocalDateTime();
            // Per la data di scadenza, convertiamo a LocalTime ma gestiamo la logica della data
            // Se la data di scadenza è in un giorno futuro rispetto a oggi, aggiungiamo 24 ore al LocalTime
            LocalDateTime oggi = LocalDateTime.now();
            java.time.LocalTime scadenzaTime = dataScadenzaDateTime.toLocalTime();
            
            // Se il biglietto scade in un giorno futuro, aggiungiamo le ore necessarie
            long giorniDifferenza = java.time.temporal.ChronoUnit.DAYS.between(oggi.toLocalDate(), dataScadenzaDateTime.toLocalDate());
            if (giorniDifferenza > 0) {
                // Aggiungiamo 24 ore per ogni giorno di differenza
                scadenzaTime = scadenzaTime.plusHours(24 * giorniDifferenza);
            }
            
            biglietto.setDataFine(scadenzaTime);
        }
        
        // Carica le tratte associate dalla tabella Biglietto_Tratta
        loadTratte(biglietto, conn);
        
        return biglietto;
    }
    
    // Carica le tratte associate al biglietto
    private static void loadTratte(Biglietto biglietto, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_TRATTE_BY_BIGLIETTO)) {
            stmt.setLong(1, biglietto.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                List<Long> idTratte = new ArrayList<>();
                List<Integer> numeroFermate = new ArrayList<>();
                
                while (rs.next()) {
                    idTratte.add(rs.getLong("id_tratta"));
                    numeroFermate.add(rs.getInt("fermate_percorse"));
                }
                
                biglietto.setId_tratte(idTratte);
                biglietto.setNumero_fermate(numeroFermate);
            }
        }
    }
    
    /**
     * Crea un Timestamp per la data di fine biglietto, gestendo correttamente i casi
     * dove la data di fine supera la mezzanotte (giorno successivo)
     * @param dataFine LocalTime che rappresenta l'ora di scadenza
     * @return Timestamp con data e ora corrette
     */
    private static Timestamp createDataFineTimestamp(java.time.LocalTime dataFine) {
        if (dataFine == null) {
            return null;
        }
        
        java.time.LocalDate oggi = java.time.LocalDate.now();
        java.time.LocalTime ora = java.time.LocalTime.now();
        
        // Se l'ora di scadenza è precedente all'ora attuale,
        // significa che il biglietto scade il giorno successivo
        if (dataFine.isBefore(ora)) {
            oggi = oggi.plusDays(1);
        }
        
        return Timestamp.valueOf(LocalDateTime.of(oggi, dataFine));
    }
    
    /**
     * Trova un biglietto guest tramite il codice
     */
    public static Biglietto findGuestTicketByCode(String ticketCode) throws SQLException {
        String sql = "SELECT * FROM Biglietto WHERE (id_utente IS NULL OR id_utente = -1) AND " +
                     "(CONCAT('OM', LPAD(id, 7, '0')) = ? OR codice_biglietto = ?)";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, ticketCode);
            ps.setString(2, ticketCode);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBiglietto(rs, con);
                }
            }
        }
        return null;
    }
    
    /**
     * Associa un biglietto guest a un utente
     */
    public static boolean associateGuestTicketToUser(Long ticketId, Long userId) throws SQLException {
        String sql = "UPDATE Biglietto SET id_utente = ? WHERE id = ? AND (id_utente IS NULL OR id_utente = -1)";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, userId);
            ps.setLong(2, ticketId);
            
            return ps.executeUpdate() > 0;
        }
    }
}

