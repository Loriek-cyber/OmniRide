package model.dao;

import model.sdata.FermataTratta;
import model.db.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FermataTrattaDAO {

    private static FermataTratta extractFermataTrattaFromResultSet(ResultSet rs) throws SQLException {
        FermataTratta ft = new FermataTratta();
        ft.setId(rs.getLong("id"));
        ft.setIdTratta(rs.getLong("id_tratta"));
        ft.setSequenza(rs.getInt("sequenza"));
        ft.setTempoProssimaFermata(rs.getInt("tempo_prossima_fermata"));
        ft.setFermata(FermataDAO.getById(rs.getLong("id_fermata")));
        return ft;
    }

    public static FermataTratta getById(Long id) throws SQLException {
        String sql = "SELECT * FROM Fermata_Tratta WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFermataTrattaFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public static void updateFermateForTratta(Long idTratta, List<FermataTratta> nuoveFermate) throws SQLException {
        String deleteSql = "DELETE FROM Fermata_Tratta WHERE id_tratta = ?";
        String insertSql = "INSERT INTO Fermata_Tratta (id_tratta, id_fermata, sequenza, tempo_prossima_fermata) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false); // Inizio transazione

            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setLong(1, idTratta);
                psDelete.executeUpdate();
            }

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                for (FermataTratta ft : nuoveFermate) {
                    psInsert.setLong(1, idTratta);
                    psInsert.setLong(2, ft.getFermata().getId());
                    psInsert.setInt(3, ft.getSequenza());
                    psInsert.setInt(4, ft.getTempoProssimaFermata());
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            }

            conn.commit(); // Conferma transazione
        } catch (SQLException e) {
            // Il try-with-resources gestirà la chiusura della connessione,
            // ma il rollback è ancora necessario in caso di errore.
            try (Connection conn = DBConnector.getConnection()) {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                e.addSuppressed(ex); // Aggiunge l'eccezione di rollback a quella originale
            }
            throw e;
        }
    }
    
    public static boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM Fermata_Tratta WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static List<FermataTratta> findFermateByTrattaId(long idTratta) throws SQLException {
        List<FermataTratta> lft = new ArrayList<>();
        String sql = "SELECT * FROM Fermata_Tratta WHERE id_tratta = ? ORDER BY sequenza";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, idTratta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()){
                    lft.add(extractFermataTrattaFromResultSet(rs));
                }
            }
        }

        if (!lft.isEmpty()) {
            for (int i = 0; i < lft.size() - 1; i++) {
                lft.get(i).setProssimaFermata(lft.get(i + 1).getFermata());
            }
        }

        return lft;
    }

    /**
     * Inserisce una nuova relazione fermata-tratta nel database
     * @param fermataTratta La relazione da inserire
     * @return L'ID della relazione inserita, null se l'inserimento fallisce
     * @throws SQLException Se c'è un errore nel database
     */
    public static Long create(FermataTratta fermataTratta) throws SQLException {
        String sql = "INSERT INTO Fermata_Tratta (id_tratta, id_fermata, tempo_prossima_fermata, sequenza) VALUES (?, ?, ?, ?)";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, fermataTratta.getIdTratta());
            ps.setLong(2, fermataTratta.getFermata().getId());
            ps.setInt(3, fermataTratta.getTempoProssimaFermata());
            ps.setInt(4, fermataTratta.getSequenza());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Inserimento fermata-tratta fallito, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Inserimento fermata-tratta fallito, nessun ID generato.");
                }
            }
        }
    }

    /**
     * Calcola la prossima sequenza per una tratta
     * @param idTratta L'ID della tratta
     * @return Il numero di sequenza successivo
     * @throws SQLException Se c'è un errore nel database
     */
    private static int getNextSequenza(Long idTratta) throws SQLException {
        String sql = "SELECT COALESCE(MAX(sequenza), 0) + 1 as next_seq FROM Fermata_Tratta WHERE id_tratta = ?";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, idTratta);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_seq");
                }
                return 1; // Prima fermata
            }
        }
    }

    /**
     * Elimina tutte le relazioni fermata-tratta per una specifica tratta
     * @param idTratta L'ID della tratta
     * @return true se l'eliminazione è riuscita, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean deleteFermateByTratta(Long idTratta) throws SQLException {
        String sql = "DELETE FROM Fermata_Tratta WHERE id_tratta = ?";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, idTratta);
            return ps.executeUpdate() >= 0;
        }
    }

    /**
     * Aggiorna una relazione fermata-tratta esistente
     * @param fermataTratta La relazione da aggiornare
     * @return true se l'aggiornamento è riuscito, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean updateFermataTratta(FermataTratta fermataTratta, int sequenza) throws SQLException {
        String sql = "UPDATE Fermata_Tratta SET tempo_prossima_fermata = ? WHERE id_tratta = ? AND id_fermata = ? AND sequenza = ?";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, fermataTratta.getTempoProssimaFermata());
            ps.setLong(2, fermataTratta.getIdTratta());
            ps.setLong(3, fermataTratta.getFermata().getId());
            ps.setInt(4, sequenza);
            return ps.executeUpdate() > 0;
        }
    }
}
