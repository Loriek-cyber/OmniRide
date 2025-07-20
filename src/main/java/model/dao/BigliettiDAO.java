package model.dao;
import model.db.DBConnector;
import model.udata.Biglietto;

import java.sql.*;
import java.time.LocalTime;
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
    private static final String SELECT_BY_USER_AND_TYPE = "SELECT * from Biglietto where id_utente = ? AND tipo = ?";

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
            }
            
            Long bigliettoId = null;
            
            // Inserisce il biglietto principale
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_BIGLIETTO, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, biglietto.getId_utente());
                stmt.setString(2, biglietto.getNome());
                stmt.setTimestamp(3, biglietto.getDataAcquisto() != null ? Timestamp.valueOf(biglietto.getDataAcquisto().atDate(java.time.LocalDate.now())) : null);
                stmt.setTimestamp(4, biglietto.getDataConvalida() != null ? Timestamp.valueOf(biglietto.getDataConvalida().atDate(java.time.LocalDate.now())) : null);
                stmt.setTimestamp(5, biglietto.getDataFine() != null ? Timestamp.valueOf(biglietto.getDataFine().atDate(java.time.LocalDate.now())) : null);
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
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER_ACTIVE)) {
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
        
        // Conversione timestamp a LocalTime
        Timestamp dataAcquisto = rs.getTimestamp("data_acquisto");
        if (dataAcquisto != null) {
            biglietto.setDataAcquisto(dataAcquisto.toLocalDateTime().toLocalTime());
        }
        
        Timestamp dataConvalida = rs.getTimestamp("data_convalida");
        if (dataConvalida != null) {
            biglietto.setDataConvalida(dataConvalida.toLocalDateTime().toLocalTime());
        }
        
        Timestamp dataScadenza = rs.getTimestamp("data_scadenza");
        if (dataScadenza != null) {
            biglietto.setDataFine(dataScadenza.toLocalDateTime().toLocalTime());
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
}

