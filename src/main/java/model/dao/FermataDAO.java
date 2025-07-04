package model.dao;
import model.db.DBConnector;
import model.sdata.Coordinate;
import model.sdata.Fermata;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FermataDAO {

    public static Fermata doRetrieveById(long id) throws SQLException {
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Fermata WHERE id = ?");
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFermataFromResultSet(rs);
                }
                return null;
            }
        }
    }

    public static List<Fermata> doRetrieveAll() throws SQLException {
        List<Fermata> fermate = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Fermata");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Fermata fermata = extractFermataFromResultSet(rs);
                if (fermata != null) {
                    fermate.add(fermata);
                }
            }
            return fermate;
        }
    }

    private static Fermata extractFermataFromResultSet(ResultSet rs) throws SQLException {
        Fermata fermata = new Fermata();
        fermata.setId(rs.getLong("id"));
        fermata.setNome(rs.getString("nome"));
        fermata.setIndirizzo(rs.getString("indirizzo"));
        fermata.setCoordinate(new Coordinate(rs.getDouble("latitudine"), rs.getDouble("longitudine")));
        String tipoStr = rs.getString("tipo");
        if (tipoStr != null && !tipoStr.isEmpty()) {
            try {
                fermata.setTipo(Fermata.TipoFermata.valueOf(tipoStr));
            } catch (IllegalArgumentException e) {
                // Se il tipo non è valido, usa un valore di default
                fermata.setTipo(Fermata.TipoFermata.FERMATA_NORMALE);
            }
        } else {
            fermata.setTipo(Fermata.TipoFermata.FERMATA_NORMALE);
        }
        fermata.setAttiva(rs.getBoolean("attiva"));
        return fermata;
    }
}
