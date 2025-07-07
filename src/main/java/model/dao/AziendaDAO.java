package model.dao;

import model.db.DBConnector;
import model.udata.Azienda;
import model.udata.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AziendaDAO {

    public static List<Azienda> doRetrieveAll() {
        List<Azienda> aziende = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Azienda");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                aziende.add(extractAziendaFromResultSet(rs));
            }
            return aziende;
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero di tutte le aziende", e);
        }
    }


    private static Azienda extractAziendaFromResultSet(ResultSet rs) throws SQLException {
        Azienda azienda = new Azienda();
        azienda.setId(rs.getLong("id"));
        azienda.setNome(rs.getString("nome"));
        String tipoStr = rs.getString("tipo");
        if (tipoStr != null) {
            azienda.setTipo(Azienda.TipoAzienda.valueOf(tipoStr));
        }
        return azienda;
    }

// CREATE
public static boolean create(Azienda nuovaAzienda) {
    String QRstr = "INSERT INTO Azienda (nome, tipo) VALUES (?, ?)";
    try (Connection con = DBConnector.getConnection()) {
        PreparedStatement ps = con.prepareStatement(QRstr);
        ps.setString(1, nuovaAzienda.getNome());
        ps.setString(2, nuovaAzienda.getTipo());

        return ps.executeUpdate() >= 1;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

// UPDATE
public static boolean update(Azienda aziendaInSessione) throws SQLException {
    String sql = "UPDATE Azienda SET nome=?, tipo=? WHERE id=?";
    try (Connection conn = DBConnector.getConnection()) {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, aziendaInSessione.getNome());
        ps.setString(2, aziendaInSessione.getTipo());
        ps.setLong(3, aziendaInSessione.getId());

        return ps.executeUpdate() >= 1;
    }
}

// FINDBYID
public static Azienda findById(long id) {
    try (Connection con = DBConnector.getConnection()) {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Azienda WHERE id = ?");
        ps.setLong(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return extractAziendaFromResultSet(rs);
            }
        }
        return null;
    } catch (SQLException e) {
        throw new RuntimeException("Errore durante il recupero dell'azienda con id: " + id, e);
    }
}
}