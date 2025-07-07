package model.dao;
import model.udata.Azienda;
import model.udata.Dipendenti;
import model.db.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DipendentiDAO{
    private static Dipendenti getDipendentifromSet(ResultSet rs) throws SQLException{
        Dipendenti dipendente = new Dipendenti();
        dipendente.setUtente(UtenteDAO.findById(rs.getLong("id_utente")));
        dipendente.setAzienda(AziendaDAO.findById(rs.getLong("id_azienda")));
        dipendente.setLavoro(Dipendenti.Lavoro.valueOf(rs.getString("lavoro")));
        return dipendente;
    }

    // CREATE
    public static boolean create(Dipendenti nuovoDipendente) {
        String QRstr = "INSERT INTO Dipendenti (id_utente, id_azienda, lavoro) VALUES (?, ?, ?)";
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement(QRstr);
            ps.setLong(1, nuovoDipendente.getUtente().getId());
            ps.setLong(2, nuovoDipendente.getAzienda().getId());
            ps.setString(3, nuovoDipendente.getLavoro().name());

            return ps.executeUpdate() >= 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE
    public static boolean update(Dipendenti dipendenteInSessione) throws SQLException {
        String sql = "UPDATE Dipendenti SET id_azienda=?, lavoro=? WHERE id_utente=?";
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, dipendenteInSessione.getAzienda().getId());
            ps.setString(2, dipendenteInSessione.getLavoro().name());
            ps.setLong(3, dipendenteInSessione.getUtente().getId());

            return ps.executeUpdate() >= 1;
        }
    }

    // FINDBYID
    public static Dipendenti findById(Long id) throws SQLException {
        String QRstr = "SELECT * FROM Dipendenti WHERE id_utente=?";
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement(QRstr);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getDipendentifromSet(rs);
            }
        }
        return null;
    }
}