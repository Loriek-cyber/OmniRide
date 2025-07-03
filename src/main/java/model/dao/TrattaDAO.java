package model.dao;
import model.db.DBConnector;
import model.sdata.*;
import model.udata.Azienda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrattaDAO {
    private static final String trattaALL = "SELECT * FROM Tratta";
    private static final String trattaByID = "SELECT * FROM Tratta WHERE id=?";

    private static Tratta getTrattaFromSET(ResultSet rs) throws SQLException {
        Azienda azienda = AziendaDAO.doRetrieveById(rs.getLong("id_azienda"));
        List<FermataTratta> fermataTrattaList = FermataTrattaDAO.getFTfromTrattaID(rs.getLong("id"));
        List<UnicaTratta> unicaTrattaList = UnicaTrattaDAO.getLUTfromIDT(rs.getLong("id"));

        return new Tratta(
                rs.getLong("id"),
                rs.getString("nome"),
                azienda,
                unicaTrattaList,
                fermataTrattaList,
                rs.getDouble("costo")
        );
    }

    public static Tratta getTrattaByID(Long id) throws SQLException {
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement ps = conn.prepareStatement(trattaByID);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getTrattaFromSET(rs);
            }
            return null; // Ritorna null se nessuna tratta viene trovata
        }
    }

    public static List<Tratta> getAllTratte() throws SQLException {
        List<Tratta> tratte = new ArrayList<>();
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(trattaALL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tratte.add(getTrattaFromSET(rs));
            }
            return tratte;
        }
    }
}