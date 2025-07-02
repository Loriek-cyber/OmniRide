package model.DAO;

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
        OrarioTratta orario = OrarioTrattaDAO.getFromIdUT(unicaTrattaId);

        return new UnicaTratta(
                unicaTrattaId,
                rs.getLong("id_tratta"),
                orario
        );
    }

    public static UnicaTratta getUTfromID(long id) throws SQLException {
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
}



