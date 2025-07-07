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

    // CREATE
    public static boolean create(Fermata nuovaFermata) {
        String QRstr = "INSERT INTO Fermata (nome, indirizzo, latitudine, longitudine, tipo, attiva) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement(QRstr);
            ps.setString(1, nuovaFermata.getNome());
            ps.setString(2, nuovaFermata.getIndirizzo());
            ps.setDouble(3, nuovaFermata.getCoordinate().getLatitudine());
            ps.setDouble(4, nuovaFermata.getCoordinate().getLongitudine());
            ps.setString(5, nuovaFermata.getTipo().name());
            ps.setBoolean(6, nuovaFermata.isAttiva());

            return ps.executeUpdate() >= 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE
    public static boolean update(Fermata fermataInSessione) throws SQLException {
        String sql = "UPDATE Fermata SET nome=?, indirizzo=?, latitudine=?, longitudine=?, tipo=?, attiva=? WHERE id=?";
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fermataInSessione.getNome());
            ps.setString(2, fermataInSessione.getIndirizzo());
            ps.setDouble(3, fermataInSessione.getCoordinate().getLatitudine());
            ps.setDouble(4, fermataInSessione.getCoordinate().getLongitudine());
            ps.setString(5, fermataInSessione.getTipo().name());
            ps.setBoolean(6, fermataInSessione.isAttiva());
            ps.setLong(7, fermataInSessione.getId());

            return ps.executeUpdate() >= 1;
        }
    }

    // FINDBYID
    public static Fermata findById(long id) throws SQLException {
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Fermata WHERE id = ?");
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFermataFromResultSet(rs);
                }
            }
            return null;
        }
    }
}