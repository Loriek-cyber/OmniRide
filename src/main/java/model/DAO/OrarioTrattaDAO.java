package model.DAO;

import model.db.DBConnector;
import model.sdata.OrarioTratta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

public class OrarioTrattaDAO {
    private static final String getOrarioFromId_UT = "SELECT ot.*, ut.id_tratta, og.giorno_settimana " +
            "FROM Orario_Tratta ot " +
            "JOIN Unica_Tratta ut ON ot.id_unica_tratta = ut.id " +
            "JOIN Orario_Giorni og ON ot.id = og.id_orario " +
            "WHERE ot.id_unica_tratta = ?;";

    private static OrarioTratta getOTfromSet(ResultSet rs) throws SQLException {
        OrarioTratta ot = null;
        Set<DayOfWeek> giorniSettimana = new HashSet<>();

        if (rs.next()) {
            ot = new OrarioTratta();
            ot.setTrattaId(rs.getLong("id_tratta"));
            ot.setOraInizio(rs.getTime("ora_inizio").toLocalTime());
            ot.setAttivo(rs.getBoolean("attivo"));
            giorniSettimana.add(DayOfWeek.valueOf(rs.getString("giorno_settimana")));

            while (rs.next()) {
                giorniSettimana.add(DayOfWeek.valueOf(rs.getString("giorno_settimana")));
            }
            ot.setGiorniSettimana(giorniSettimana);
        }
        return ot;
    }

    public static OrarioTratta getFromIdUT(Long id) {
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(getOrarioFromId_UT);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            return getOTfromSet(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
