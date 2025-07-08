package model.dao;

import model.db.DBConnector;
import model.sdata.OrarioTratta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrarioTrattaDAO {

    /**
     * Estrae un oggetto OrarioTratta da un ResultSet.
     * Metodo helper per evitare duplicazione di codice.
     */
    private static OrarioTratta extractOrarioFromResultSet(ResultSet rs) throws SQLException {
        OrarioTratta orario = new OrarioTratta();
        
        orario.setId(rs.getLong("id"));
        orario.setTrattaId(rs.getLong("id_tratta"));
        orario.setOraPartenza(rs.getTime("ora_partenza").toLocalTime());
        
        Time oraArrivo = rs.getTime("ora_arrivo");
        if (oraArrivo != null) {
            orario.setOraArrivo(oraArrivo.toLocalTime());
        }
        
        orario.setGiorniSettimana(rs.getString("giorni_settimana"));
        
        try {
            orario.setTipoServizio(OrarioTratta.TipoServizio.valueOf(rs.getString("tipo_servizio")));
        } catch (IllegalArgumentException e) {
            orario.setTipoServizio(OrarioTratta.TipoServizio.NORMALE); // Default
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
     * Inserisce un nuovo orario nel database.
     *
     * @param nuovoOrarioTratta L'oggetto OrarioTratta da creare.
     * @return L'ID del nuovo orario creato.
     * @throws SQLException in caso di errore del database.
     */
    public static Long create(OrarioTratta nuovoOrarioTratta) throws SQLException {
        String sql = "INSERT INTO Tratta_Orari (id_tratta, ora_partenza, ora_arrivo, giorni_settimana, " +
                     "tipo_servizio, frequenza_minuti, attivo, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, nuovoOrarioTratta.getTrattaId());
            ps.setTime(2, Time.valueOf(nuovoOrarioTratta.getOraPartenza()));
            ps.setTime(3, nuovoOrarioTratta.getOraArrivo() != null ? Time.valueOf(nuovoOrarioTratta.getOraArrivo()) : null);
            ps.setString(4, nuovoOrarioTratta.getGiorniSettimana());
            ps.setString(5, nuovoOrarioTratta.getTipoServizio().name());
            
            if (nuovoOrarioTratta.getFrequenzaMinuti() != null) {
                ps.setInt(6, nuovoOrarioTratta.getFrequenzaMinuti());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            
            ps.setBoolean(7, nuovoOrarioTratta.isAttivo());
            ps.setString(8, nuovoOrarioTratta.getNote());
            
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Creazione orario fallita, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creazione orario fallita, nessun ID generato.");
                }
            }
        }
    }

    /**
     * Aggiorna un orario esistente.
     *
     * @param OrarioTrattaInSessione L'oggetto OrarioTratta con i dati aggiornati.
     * @return true se l'aggiornamento ha avuto successo.
     * @throws SQLException in caso di errore del database.
     */
    public static boolean update(OrarioTratta OrarioTrattaInSessione) throws SQLException {
        String sql = "UPDATE Tratta_Orari SET ora_partenza = ?, ora_arrivo = ?, giorni_settimana = ?, " +
                     "tipo_servizio = ?, frequenza_minuti = ?, attivo = ?, note = ? WHERE id = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTime(1, Time.valueOf(OrarioTrattaInSessione.getOraPartenza()));
            ps.setTime(2, OrarioTrattaInSessione.getOraArrivo() != null ? Time.valueOf(OrarioTrattaInSessione.getOraArrivo()) : null);
            ps.setString(3, OrarioTrattaInSessione.getGiorniSettimana());
            ps.setString(4, OrarioTrattaInSessione.getTipoServizio().name());
            
            if (OrarioTrattaInSessione.getFrequenzaMinuti() != null) {
                ps.setInt(5, OrarioTrattaInSessione.getFrequenzaMinuti());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            
            ps.setBoolean(6, OrarioTrattaInSessione.isAttivo());
            ps.setString(7, OrarioTrattaInSessione.getNote());
            ps.setLong(8, OrarioTrattaInSessione.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Trova un orario specifico tramite il suo ID.
     *
     * @param id L'ID dell'orario da cercare.
     * @return Un oggetto OrarioTratta se trovato, altrimenti null.
     * @throws SQLException in caso di errore del database.
     */
    public static OrarioTratta getById(Long id) throws SQLException {
        String sql = "SELECT * FROM Tratta_Orari WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractOrarioFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Disattiva (elimina logicamente) un orario impostando il suo stato a non attivo.
     *
     * @param id L'ID dell'orario da disattivare.
     * @return true se la disattivazione ha avuto successo.
     * @throws SQLException in caso di errore del database.
     */
    public static boolean deactivateOrarioTratta(Long id) throws SQLException {
        String sql = "UPDATE Tratta_Orari SET attivo = 0 WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Recupera tutti gli orari attivi di una specifica tratta.
     *
     * @param trattaId L'ID della tratta.
     * @return Una lista di OrarioTratta, ordinata per ora di partenza.
     * @throws SQLException in caso di errore del database.
     */
    public static List<OrarioTratta> findOrariByTrattaId(Long trattaId) throws SQLException {
        List<OrarioTratta> orari = new ArrayList<>();
        String sql = "SELECT * FROM Tratta_Orari WHERE id_tratta = ? AND attivo = 1 ORDER BY ora_partenza";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, trattaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orari.add(extractOrarioFromResultSet(rs));
                }
            }
        }
        return orari;
    }
}