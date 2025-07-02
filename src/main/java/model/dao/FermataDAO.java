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
                return extractFermataFromResultSet(rs);
            }
        }
    }

    public static List<Fermata> doRetrieveAll() throws SQLException {
        List<Fermata> fermate = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Fermata");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                fermate.add(extractFermataFromResultSet(rs));
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
        if (tipoStr != null) {
            fermata.setTipo(Fermata.TipoFermata.valueOf(tipoStr));
        }
        fermata.setAttiva(rs.getBoolean("attiva"));
        return fermata;
    }
}
