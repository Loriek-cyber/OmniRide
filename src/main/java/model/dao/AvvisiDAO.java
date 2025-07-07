package model.dao;

import com.mysql.cj.jdbc.PreparedStatementWrapper;
import model.db.DBConnector;
import model.sdata.Avvisi;
import model.sdata.Coordinate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AvvisiDAO {

    private static Avvisi getAvvisiFromSet(ResultSet rs) throws SQLException {
        Avvisi avvisi = new Avvisi();
        avvisi.setId(rs.getLong("id"));
        avvisi.setDescrizione(rs.getString("descrizione"));
        List<Long> data = new ArrayList<>();
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement pr = conn.prepareStatement("SELECT * FROM Avvisi_tratte WHERE avviso_id = ?");
            pr.setLong(1, avvisi.getId());
            ResultSet rs2 = pr.executeQuery();
            while(rs2.next()){
                data.add(rs2.getLong("tratta_id"));
            }
            avvisi.setId_tratte_coinvolte(data);
        }
        return avvisi;
        }


    public static List<Avvisi> getAllAvvisi() throws SQLException {
        List<Avvisi> avvisi = new ArrayList<>();
        try(Connection conn = DBConnector.getConnection()){//connessione al database
            PreparedStatement pr = conn.prepareStatement("SELECT * FROM Avvisi");
            ResultSet rs = pr.executeQuery();
            while(rs.next()){
                avvisi.add(getAvvisiFromSet(rs));
            }
        }
        return avvisi;
    }

    public static List<Avvisi> getAvvisiByTrattaId(Long id) throws SQLException {
        List<Avvisi> avvisi = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection()){
            String sql = "SELECT * " +
                    "a.id AS avviso_id," +
                    "a.descrizione," +
                    "t.id AS tratta_id," +
                    "t.nome AS nome_tratta" +
                    "FROM Avvisi a " +
                    "JOIN Avvisi_tratte at ON a.id = at.avviso_id " +
                    "JOIN Tratta t ON at.tratta_id = t.id " +
                    "WHERE t.id = ?";
            PreparedStatement pr = conn.prepareStatement(sql);
            pr.setLong(1, id);
            ResultSet rs = pr.executeQuery();
            while(rs.next()){
                avvisi.add(getAvvisiFromSet(rs));
            }
            return avvisi;
            }
        }

// CREATE
public static boolean create(Avvisi nuovoAvviso) throws SQLException {
    String QRstr = "INSERT INTO Avvisi (descrizione) VALUES (?)";
    String QRfor = "INSERT INTO Avvisi_tratte (avviso_id, tratta_id) VALUES (?,?)";
    try (Connection con = DBConnector.getConnection()) {
        PreparedStatement ps = con.prepareStatement(QRstr, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, nuovoAvviso.getDescrizione());
        int affectedRows = ps.executeUpdate();

        if (affectedRows == 0) {
            return false;
        }

        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                long avvisoId = generatedKeys.getLong(1);
                for (Long id_tratta : nuovoAvviso.getId_tratte_coinvolte()) {
                    PreparedStatement ps2 = con.prepareStatement(QRfor);
                    ps2.setLong(1, avvisoId);
                    ps2.setLong(2, id_tratta);
                    ps2.executeUpdate();
                }
            } else {
                throw new SQLException("Creazione avviso fallita, nessun ID ottenuto.");
            }
        }
        return true;
    }
}

// UPDATE
public static boolean update(Avvisi avvisoInSessione) throws SQLException {
    String sql = "UPDATE Avvisi SET descrizione=? WHERE id=?";
    String deleteSql = "DELETE FROM Avvisi_tratte WHERE avviso_id = ?";
    String insertSql = "INSERT INTO Avvisi_tratte (avviso_id, tratta_id) VALUES (?,?)";

    try (Connection con = DBConnector.getConnection()) {
        // Update descrizione
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, avvisoInSessione.getDescrizione());
        ps.setLong(2, avvisoInSessione.getId());
        boolean updated = ps.executeUpdate() >= 1;

        // Delete old tratte
        PreparedStatement deletePs = con.prepareStatement(deleteSql);
        deletePs.setLong(1, avvisoInSessione.getId());
        deletePs.executeUpdate();

        // Insert new tratte
        for (Long id_tratta : avvisoInSessione.getId_tratte_coinvolte()) {
            PreparedStatement insertPs = con.prepareStatement(insertSql);
            insertPs.setLong(1, avvisoInSessione.getId());
            insertPs.setLong(2, id_tratta);
            insertPs.executeUpdate();
        }
        return updated;
    }
}

// FINDBYID
public static Avvisi findById(Long id) throws SQLException {
    try (Connection conn = DBConnector.getConnection()) {
        PreparedStatement pr = conn.prepareStatement("SELECT * FROM Avvisi WHERE id = ?");
        pr.setLong(1, id);
        ResultSet rs = pr.executeQuery();
        if (rs.next()) {
            return getAvvisiFromSet(rs);
        }
        return null;
    }
}
}