package model.DAO;

import model.db.DBConnector;
import model.udata.Azienda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AziendaDAO {

    public static Azienda doRetrieveById(long id) {
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
}
