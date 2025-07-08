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
    
    public static boolean deleteFermataTratta(Long id) throws SQLException {
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
}
