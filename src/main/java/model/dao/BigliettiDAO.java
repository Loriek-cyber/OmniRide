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
        biglietto.setStato(Biglietto.StatoBiglietto.valueOf(rs.getString("stato")));
        return  biglietto;

    }

    public static Biglietto GetBigliettoFromID(Long id) throws  SQLException {
        try(Connection conn = DBConnector.getConnection()){
            String sql = "SELECT * FROM Biglietto WHERE id = ?";
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
        String sql = "SELECT * FROM Biglietto WHERE id_utente = ?";
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
        String sql = "SELECT * FROM Biglietto WHERE id_tratta = ?";
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
    public static Long insertBiglietto(Biglietto biglietto) throws SQLException {
        String sql = "INSERT INTO Biglietto (id_utente, id_tratta, data_acquisto, prezzo, stato) VALUES (?, ?, ?, ?, ?)";
        
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
        String sql = "UPDATE Biglietto SET stato = ? WHERE id = ?";
        
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
        String sql = "UPDATE Biglietto SET stato = ?, data_convalida = CURRENT_TIMESTAMP WHERE id = ? AND stato = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, Biglietto.StatoBiglietto.CONVALIDATO.name());
            ps.setLong(2, id);
            ps.setString(3, Biglietto.StatoBiglietto.ACQUISTATO.name());
            
            return ps.executeUpdate() > 0;
        }
    }

}


