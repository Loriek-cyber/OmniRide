package model.dao;

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

    private static OrarioTratta getOTfromSet(ResultSet rs) throws SQLException {
        OrarioTratta ot = null;
        Set<DayOfWeek> giorniSettimana = new HashSet<>();
        long currentOrarioId = -1;

        while (rs.next()) {
            if (ot == null) { // First row
                ot = new OrarioTratta();
                currentOrarioId = rs.getLong("id");
                ot.setId(currentOrarioId);
                ot.setUnicaTrattaId(rs.getLong("id_unica_tratta"));
                ot.setTrattaId(rs.getLong("id_tratta"));
                ot.setOraInizio(rs.getTime("ora_inizio").toLocalTime());
                ot.setAttivo(rs.getBoolean("attivo"));
            }
            giorniSettimana.add(DayOfWeek.valueOf(rs.getString("giorno_settimana")));
        }

        if (ot != null) {
            ot.setGiorniSettimana(giorniSettimana);
        }
        return ot;
    }

    // CREATE
    public static boolean create(OrarioTratta nuovoOrarioTratta) {
        String QRstr = "INSERT INTO Orario_Tratta (id_unica_tratta, ora_inizio, attivo) VALUES (?, ?, ?)";
        String QRgiorni = "INSERT INTO Orario_Giorni (id_orario, giorno_settimana) VALUES (?, ?)";
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement(QRstr, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, nuovoOrarioTratta.getUnicaTrattaId());
            ps.setTime(2, java.sql.Time.valueOf(nuovoOrarioTratta.getOraInizio()));
            ps.setBoolean(3, nuovoOrarioTratta.isAttivo());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long orarioId = generatedKeys.getLong(1);
                    for (java.time.DayOfWeek giorno : nuovoOrarioTratta.getGiorniSettimana()) {
                        PreparedStatement psGiorni = con.prepareStatement(QRgiorni);
                        psGiorni.setLong(1, orarioId);
                        psGiorni.setString(2, giorno.name());
                        psGiorni.executeUpdate();
                    }
                } else {
                    throw new SQLException("Creazione orario fallita, nessun ID ottenuto.");
                }
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE
    public static boolean update(OrarioTratta orarioTrattaInSessione) throws SQLException {
        String sql = "UPDATE Orario_Tratta SET ora_inizio=?, attivo=? WHERE id=?";
        String deleteGiorniSql = "DELETE FROM Orario_Giorni WHERE id_orario = ?";
        String insertGiorniSql = "INSERT INTO Orario_Giorni (id_orario, giorno_settimana) VALUES (?, ?)";
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setTime(1, java.sql.Time.valueOf(orarioTrattaInSessione.getOraInizio()));
            ps.setBoolean(2, orarioTrattaInSessione.isAttivo());
            ps.setLong(3, orarioTrattaInSessione.getId());
            boolean updated = ps.executeUpdate() >= 1;

            PreparedStatement deletePs = conn.prepareStatement(deleteGiorniSql);
            deletePs.setLong(1, orarioTrattaInSessione.getId());
            deletePs.executeUpdate();

            for (java.time.DayOfWeek giorno : orarioTrattaInSessione.getGiorniSettimana()) {
                PreparedStatement insertPs = conn.prepareStatement(insertGiorniSql);
                insertPs.setLong(1, orarioTrattaInSessione.getId());
                insertPs.setString(2, giorno.name());
                insertPs.executeUpdate();
            }

            return updated;
        }
    }

    // FINDBYID
    public static OrarioTratta findById(Long id) {
        String sql = "SELECT ot.id, ot.id_unica_tratta, ot.ora_inizio, ot.attivo, ut.id_tratta, og.giorno_settimana " +
            "FROM Orario_Tratta ot " +
            "JOIN Unica_Tratta ut ON ot.id_unica_tratta = ut.id " +
            "JOIN Orario_Giorni og ON ot.id = og.id_orario " +
            "WHERE ot.id = ?;";
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            return getOTfromSet(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static OrarioTratta findByUnicaTrattaId(Long unicaTrattaId) {
        String sql = "SELECT ot.id, ot.id_unica_tratta, ot.ora_inizio, ot.attivo, ut.id_tratta, og.giorno_settimana " +
            "FROM Orario_Tratta ot " +
            "JOIN Unica_Tratta ut ON ot.id_unica_tratta = ut.id " +
            "JOIN Orario_Giorni og ON ot.id = og.id_orario " +
            "WHERE ot.id_unica_tratta = ?;";
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, unicaTrattaId);
            ResultSet rs = ps.executeQuery();
            return getOTfromSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
