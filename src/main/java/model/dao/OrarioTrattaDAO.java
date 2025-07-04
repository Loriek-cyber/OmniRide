package model.dao;

import model.db.DBConnector;
import model.sdata.OrarioTratta;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object per la gestione degli orari delle tratte.
 * Gestisce la nuova struttura semplificata con tabella Tratta_Orari.
 */
public class OrarioTrattaDAO {
    
    private static final String GET_ORARI_BY_TRATTA = 
        "SELECT * FROM Tratta_Orari WHERE id_tratta = ? AND attivo = 1 ORDER BY ora_partenza";
    
    private static final String GET_ORARIO_BY_ID = 
        "SELECT * FROM Tratta_Orari WHERE id = ?";
    
    private static final String INSERT_ORARIO = 
        "INSERT INTO Tratta_Orari (id_tratta, ora_partenza, ora_arrivo, giorni_settimana, " +
        "tipo_servizio, frequenza_minuti, attivo, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_ORARIO = 
        "UPDATE Tratta_Orari SET ora_partenza = ?, ora_arrivo = ?, giorni_settimana = ?, " +
        "tipo_servizio = ?, frequenza_minuti = ?, attivo = ?, note = ? WHERE id = ?";
    
    private static final String DELETE_ORARIO = 
        "UPDATE Tratta_Orari SET attivo = 0 WHERE id = ?";
    
    private static final String GET_ORARI_BY_GIORNO = 
        "SELECT * FROM Tratta_Orari WHERE id_tratta = ? AND giorni_settimana LIKE ? AND attivo = 1";
    
    /**
     * Estrae un OrarioTratta dal ResultSet
     */
    private static OrarioTratta getOrarioFromResultSet(ResultSet rs) throws SQLException {
        OrarioTratta orario = new OrarioTratta();
        
        orario.setId(rs.getLong("id"));
        orario.setTrattaId(rs.getLong("id_tratta"));
        orario.setOraPartenza(rs.getTime("ora_partenza").toLocalTime());
        
        Time oraArrivo = rs.getTime("ora_arrivo");
        if (oraArrivo != null) {
            orario.setOraArrivo(oraArrivo.toLocalTime());
        }
        
        orario.setGiorniSettimana(rs.getString("giorni_settimana"));
        
        String tipoServizio = rs.getString("tipo_servizio");
        if (tipoServizio != null) {
            try {
                orario.setTipoServizio(OrarioTratta.TipoServizio.valueOf(tipoServizio));
            } catch (IllegalArgumentException e) {
                orario.setTipoServizio(OrarioTratta.TipoServizio.NORMALE);
            }
        }
        
        orario.setFrequenzaMinuti(rs.getInt("frequenza_minuti"));
        if (rs.wasNull()) {
            orario.setFrequenzaMinuti(null);
        }
        
        orario.setAttivo(rs.getBoolean("attivo"));
        orario.setNote(rs.getString("note"));
        
        return orario;
    }
    
    /**
     * Recupera tutti gli orari di una tratta
     */
    public static List<OrarioTratta> getOrariByTrattaId(Long trattaId) throws SQLException {
        List<OrarioTratta> orari = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(GET_ORARI_BY_TRATTA);
            ps.setLong(1, trattaId);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orari.add(getOrarioFromResultSet(rs));
            }
        }
        
        return orari;
    }
    
    /**
     * Recupera un orario specifico per ID
     */
    public static OrarioTratta getOrarioById(Long id) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(GET_ORARIO_BY_ID);
            ps.setLong(1, id);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getOrarioFromResultSet(rs);
            }
        }
        return null;
    }
    
    /**
     * Recupera gli orari di una tratta per un giorno specifico
     */
    public static List<OrarioTratta> getOrariByTrattaAndGiorno(Long trattaId, String giorno) throws SQLException {
        List<OrarioTratta> orari = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(GET_ORARI_BY_GIORNO);
            ps.setLong(1, trattaId);
            ps.setString(2, "%" + giorno.toUpperCase() + "%");
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orari.add(getOrarioFromResultSet(rs));
            }
        }
        
        return orari;
    }
    
    /**
     * Inserisce un nuovo orario
     */
    public static Long insertOrario(OrarioTratta orario) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(INSERT_ORARIO, Statement.RETURN_GENERATED_KEYS);
            
            ps.setLong(1, orario.getTrattaId());
            ps.setTime(2, Time.valueOf(orario.getOraPartenza()));
            ps.setTime(3, orario.getOraArrivo() != null ? Time.valueOf(orario.getOraArrivo()) : null);
            ps.setString(4, orario.getGiorniSettimana());
            ps.setString(5, orario.getTipoServizio().name());
            
            if (orario.getFrequenzaMinuti() != null) {
                ps.setInt(6, orario.getFrequenzaMinuti());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            
            ps.setBoolean(7, orario.isAttivo());
            ps.setString(8, orario.getNote());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserimento orario fallito, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Inserimento orario fallito, nessun ID generato.");
                }
            }
        }
    }
    
    /**
     * Aggiorna un orario esistente
     */
    public static boolean updateOrario(OrarioTratta orario) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(UPDATE_ORARIO);
            
            ps.setTime(1, Time.valueOf(orario.getOraPartenza()));
            ps.setTime(2, orario.getOraArrivo() != null ? Time.valueOf(orario.getOraArrivo()) : null);
            ps.setString(3, orario.getGiorniSettimana());
            ps.setString(4, orario.getTipoServizio().name());
            
            if (orario.getFrequenzaMinuti() != null) {
                ps.setInt(5, orario.getFrequenzaMinuti());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            
            ps.setBoolean(6, orario.isAttivo());
            ps.setString(7, orario.getNote());
            ps.setLong(8, orario.getId());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Elimina logicamente un orario (lo disattiva)
     */
    public static boolean deleteOrario(Long id) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(DELETE_ORARIO);
            ps.setLong(1, id);
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Metodo di compatibilità con la vecchia struttura
     * @deprecated Usa getOrariByTrattaId invece
     */
    @Deprecated
    public static OrarioTratta getFromIdUT(Long id) {
        try {
            return getOrarioById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
