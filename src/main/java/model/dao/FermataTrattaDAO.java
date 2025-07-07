package model.dao;
import model.sdata.Fermata;
import model.sdata.FermataTratta;
import model.db.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FermataTrattaDAO {
    private static final String getFTfromTid = "SELECT * FROM Fermata_Tratta WHERE id_tratta = ? ORDER BY sequenza";

    private static FermataTratta getFTfromSet(ResultSet rs) throws SQLException {
        FermataTratta ft = new FermataTratta(
                rs.getLong("id_tratta"),
                FermataDAO.findById(rs.getLong("id_fermata")),
                null, // La prossima fermata verrà impostata dopo
                rs.getInt("tempo_prossima_fermata")
        );
        ft.setSequenza(rs.getInt("sequenza"));
        return ft;
    }

    public static List<FermataTratta> getFTfromTrattaID(long id_tratta) throws SQLException {
        List<FermataTratta> lft = new ArrayList<>();
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement ps = conn.prepareStatement(getFTfromTid);
            ps.setLong(1, id_tratta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                lft.add(getFTfromSet(rs));
            }
        }

        // Imposta la fermata successiva per ogni elemento della lista
        if (!lft.isEmpty()) {
            for (int i = 0; i < lft.size() - 1; i++) {
                lft.get(i).setProssimaFermata(lft.get(i + 1).getFermata());
            }
            // L'ultima fermata non ha una prossima fermata, quindi impostiamo a null esplicitamente
            lft.get(lft.size() - 1).setProssimaFermata(null);
        }

        return lft;
    }

    // CREATE
    public static boolean create(FermataTratta nuovaFermataTratta) {
        String QRstr = "INSERT INTO Fermata_Tratta (id_tratta, id_fermata, sequenza, tempo_prossima_fermata) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement(QRstr);
            ps.setLong(1, nuovaFermataTratta.getId_tratta());
            ps.setLong(2, nuovaFermataTratta.getFermata().getId());
            ps.setInt(3, nuovaFermataTratta.getSequenza());
            ps.setInt(4, nuovaFermataTratta.getTempo_prossima_fermata());

            return ps.executeUpdate() >= 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE
    public static boolean update(FermataTratta fermataTrattaInSessione) throws SQLException {
        String sql = "UPDATE Fermata_Tratta SET sequenza=?, tempo_prossima_fermata=? WHERE id_tratta=? AND id_fermata=?";
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, fermataTrattaInSessione.getSequenza());
            ps.setInt(2, fermataTrattaInSessione.getTempo_prossima_fermata());
            ps.setLong(3, fermataTrattaInSessione.getId_tratta());
            ps.setLong(4, fermataTrattaInSessione.getFermata().getId());

            return ps.executeUpdate() >= 1;
        }
    }

    // FINDBYID - Nota: FermataTratta ha una chiave primaria composta (id_tratta, id_fermata)
    public static FermataTratta findById(long id_tratta, long id_fermata) throws SQLException {
        String QRstr = "SELECT * FROM Fermata_Tratta WHERE id_tratta = ? AND id_fermata = ?";
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement(QRstr);
            ps.setLong(1, id_tratta);
            ps.setLong(2, id_fermata);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getFTfromSet(rs);
            }
        }
        return null;
    }
}
