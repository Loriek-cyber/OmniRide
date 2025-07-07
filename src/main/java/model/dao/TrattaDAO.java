package model.dao;
import model.db.DBConnector;
import model.sdata.*;
import model.udata.Azienda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrattaDAO {
    private static final String trattaALL = "SELECT * FROM Tratta";
    private static final String trattaByID = "SELECT * FROM Tratta WHERE id=?";

    private static Tratta getTrattaFromSET(ResultSet rs) throws SQLException {
        Azienda azienda = AziendaDAO.findById(rs.getLong("id_azienda"));
        List<FermataTratta> fermataTrattaList = FermataTrattaDAO.getFTfromTrattaID(rs.getLong("id"));
        List<UnicaTratta> unicaTrattaList = UnicaTrattaDAO.getLUTfromIDT(rs.getLong("id"));

        return new Tratta(
                rs.getLong("id"),
                rs.getString("nome"),
                azienda,
                unicaTrattaList,
                fermataTrattaList,
                rs.getDouble("costo")
        );
    }

    public static List<Tratta> getAllTratte() throws SQLException {
        List<Tratta> tratte = new ArrayList<>();
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(trattaALL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tratte.add(getTrattaFromSET(rs));
            }
            return tratte;
        }
    }

    public static String getTrattaNameByID(Long id){
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(trattaByID);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nome");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    // CREATE
    public static boolean create(Tratta nuovaTratta) {
        String QRstr = "INSERT INTO Tratta (nome, id_azienda, costo) VALUES (?, ?, ?)";
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement(QRstr);
            ps.setString(1, nuovaTratta.getNome());
            ps.setLong(2, nuovaTratta.getAzienda().getId());
            ps.setDouble(3, nuovaTratta.getCosto());

            return ps.executeUpdate() >= 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE
    public static boolean update(Tratta trattaInSessione) throws SQLException {
        String sql = "UPDATE Tratta SET nome=?, id_azienda=?, costo=? WHERE id=?";
        try (Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, trattaInSessione.getNome());
            ps.setLong(2, trattaInSessione.getAzienda().getId());
            ps.setDouble(3, trattaInSessione.getCosto());
            ps.setLong(4, trattaInSessione.getId());

            return ps.executeUpdate() >= 1;
        }
    }

    // FINDBYID
    public static Tratta findById(Long id) throws SQLException {
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement ps = conn.prepareStatement(trattaByID);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getTrattaFromSET(rs);
            }
            return null; // Ritorna null se nessuna tratta viene trovata
        }
    }
}
