package model.dao;

import model.db.DBConnector;
import model.sdata.OrarioTratta;
import model.sdata.UnicaTratta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnicaTrattaDAO {
    private static final String getUnicaFromID = "SELECT * FROM Unica_Tratta WHERE id = ?";
    private static final String getAllUTFromIDT = "SELECT * FROM Unica_Tratta WHERE id_tratta = ?";

    private static UnicaTratta getUTfromSet(ResultSet rs) throws SQLException {
        long unicaTrattaId = rs.getLong("id");
        OrarioTratta orario = OrarioTrattaDAO.findByUnicaTrattaId(unicaTrattaId);

        return new UnicaTratta(
                unicaTrattaId,
                rs.getLong("id_tratta"),
                orario
        );
    }

    public static List<UnicaTratta> getLUTfromIDT(long id) throws SQLException {
        List<UnicaTratta> utl = new ArrayList<>();
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement pr = con.prepareStatement(getAllUTFromIDT);
            pr.setLong(1, id);
            ResultSet rs = pr.executeQuery();
            while(rs.next()){
                utl.add(getUTfromSet(rs));
            }
        }
        return utl;
    }

    // CREATE
    public static boolean create(UnicaTratta nuovaUnicaTratta) {
        String QRstr = "INSERT INTO Unica_Tratta (id_tratta) VALUES (?)";
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement(QRstr);
            ps.setLong(1, nuovaUnicaTratta.getTrattaId());

            return ps.executeUpdate() >= 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE
    public static boolean update(UnicaTratta unicaTrattaInSessione) throws SQLException {
        // Non c'è nulla da aggiornare in Unica_Tratta se non l'id_tratta,
        // che non dovrebbe cambiare. Se necessario, si può implementare.
        return true;
    }

    // FINDBYID
    public static UnicaTratta findById(long id) throws SQLException {
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement ps = conn.prepareStatement(getUnicaFromID);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return getUTfromSet(rs);
            }
        }
        return null;
    }
}
