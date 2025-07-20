package model.dao;
import model.db.DBConnector;
import model.udata.Biglietto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BigliettiDAO {
    public static Biglietto getBfromSet(ResultSet rs)throws SQLException {
        Biglietto biglietto = new Biglietto();
        biglietto.setId(rs.getLong("id"));
        biglietto.setId_utente(rs.getLong("id_utente"));
        biglietto.setId_tratta(rs.getLong("id_tratta"));
        biglietto.setDataAquisto(rs.getTimestamp("data_acquisto"));
        biglietto.setDataConvalida(rs.getTimestamp("data_convalida"));
        biglietto.setDataScadenza(rs.getTimestamp("data_scadenza"));
        biglietto.setStato(Biglietto.StatoBiglietto.valueOf(rs.getString("stato")));
        biglietto.setPrezzo(rs.getDouble("prezzo_pagato"));
        return  biglietto;

    }

    public static Biglietto getById(Long id) throws SQLException {
        try(Connection conn = DBConnector.getConnection()){
            String sql = "SELECT * FROM biglietto WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return getBfromSet(rs);
            }
            return null;
        }
    }

    public static List<Biglietto> GetBigliettoFromUserID(Long id) throws SQLException{
        String sql = "SELECT * FROM biglietto WHERE id_utente = ? ORDER BY data_acquisto DESC";
        try (Connection conn = DBConnector.getConnection()){
            List<Biglietto> biglietti = new ArrayList<Biglietto>();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                biglietti.add(getBfromSet(rs));
            }
            return biglietti;
            }
        }

    public static List<Biglietto> GetBigliettoFromTrattaID(Long id) throws SQLException{
        String sql = "SELECT * FROM biglietto WHERE id_tratta = ?";
        try (Connection conn = DBConnector.getConnection()){
            List<Biglietto> biglietti = new ArrayList<Biglietto>();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                biglietti.add(getBfromSet(rs));
            }
            return biglietti;
        }
    }

    /**
     * Inserisce un nuovo biglietto nel database
     * @param biglietto Il biglietto da inserire
     * @return L'ID del biglietto inserito, null se l'inserimento fallisce
     * @throws SQLException Se c'è un errore nel database
     */
    public static Long create(Biglietto biglietto) throws SQLException {
        String sql = "INSERT INTO biglietto (id_utente, id_tratta, data_acquisto, prezzo_pagato, stato) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, biglietto.getId_utente());
            ps.setLong(2, biglietto.getId_tratta());
            ps.setTimestamp(3, biglietto.getDataAquisto());
            ps.setDouble(4, biglietto.getPrezzo());
            ps.setString(5, biglietto.getStato().name());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Inserimento biglietto fallito, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Inserimento biglietto fallito, nessun ID generato.");
                }
            }
        }
    }

    /**
     * Aggiorna lo stato di un biglietto
     * @param id L'ID del biglietto
     * @param stato Il nuovo stato
     * @return true se l'aggiornamento è riuscito, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean updateStatoBiglietto(Long id, Biglietto.StatoBiglietto stato) throws SQLException {
        String sql = "UPDATE biglietto SET stato = ? WHERE id = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, stato.name());
            ps.setLong(2, id);
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Convalida un biglietto
     * @param id L'ID del biglietto
     * @return true se la convalida è riuscita, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean convalidaBiglietto(Long id) throws SQLException {
        String sql = "UPDATE biglietto SET stato = ?, data_convalida = CURRENT_TIMESTAMP, data_scadenza = DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 2 HOUR) WHERE id = ? AND stato = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, Biglietto.StatoBiglietto.CONVALIDATO.name());
            ps.setLong(2, id);
            ps.setString(3, Biglietto.StatoBiglietto.ACQUISTATO.name());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Aggiorna un biglietto completo
     * @param biglietto Il biglietto da aggiornare
     * @return true se l'aggiornamento è riuscito, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean update(Biglietto biglietto) throws SQLException {
        String sql = "UPDATE biglietto SET id_utente = ?, id_tratta = ?, data_acquisto = ?, data_convalida = ?, prezzo_pagato = ?, stato = ? WHERE id = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, biglietto.getId_utente());
            ps.setLong(2, biglietto.getId_tratta());
            ps.setTimestamp(3, biglietto.getDataAquisto());
            ps.setTimestamp(4, biglietto.getDataConvalida());
            ps.setDouble(5, biglietto.getPrezzo());
            ps.setString(6, biglietto.getStato().name());
            ps.setLong(7, biglietto.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un biglietto dal database
     * @param id L'ID del biglietto da eliminare
     * @return true se l'eliminazione è riuscita, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM biglietto WHERE id = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Recupera tutti i biglietti dal database
     * @return Lista di tutti i biglietti
     * @throws SQLException Se c'è un errore nel database
     */
    public static List<Biglietto> getAll() throws SQLException {
        String sql = "SELECT * FROM biglietto ORDER BY data_acquisto DESC";
        List<Biglietto> biglietti = new ArrayList<>();
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                biglietti.add(getBfromSet(rs));
            }
            return biglietti;
        }
    }

}

