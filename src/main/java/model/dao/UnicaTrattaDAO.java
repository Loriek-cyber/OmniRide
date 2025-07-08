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
        OrarioTratta orario = OrarioTrattaDAO.getById(rs.getLong("id_orario"));

        return new UnicaTratta(
                unicaTrattaId,
                rs.getLong("id_tratta"),
                orario
        );
    }

    public static UnicaTratta getById(long id) throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(getUnicaFromID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                    return getUTfromSet(rs);
                }
            }
        }
        return null;
    }

    public static Long create(UnicaTratta nuovaUnicaTratta) throws SQLException {
        String sql = "INSERT INTO Unica_Tratta (id_tratta, id_orario) VALUES (?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, nuovaUnicaTratta.getTrattaId());
            ps.setLong(2, nuovaUnicaTratta.getOrarioTratta().getId());

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Creazione UnicaTratta fallita, nessuna riga modificata.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creazione UnicaTratta fallita, nessun ID ottenuto.");
                }
            }
        }
    }

    public static boolean update(UnicaTratta unicaTrattaInSessione) throws SQLException {
        String sql = "UPDATE Unica_Tratta SET id_tratta = ?, id_orario = ? WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, unicaTrattaInSessione.getTrattaId());
            ps.setLong(2, unicaTrattaInSessione.getOrarioTratta().getId());
            ps.setLong(3, unicaTrattaInSessione.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public static List<UnicaTratta> getLUTfromIDT(long id) throws SQLException {
        List<UnicaTratta> utl = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement pr = con.prepareStatement(getAllUTFromIDT)) {
            pr.setLong(1, id);
            try (ResultSet rs = pr.executeQuery()) {
                while(rs.next()){
                    utl.add(getUTfromSet(rs));
                }
            }
        }
        return utl;
    }
}