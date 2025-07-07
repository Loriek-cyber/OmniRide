package model.dao;
import model.db.DBConnector;
import model.udata.Biglietto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BigliettiDAO {
    public static Biglietto getBfromSet(ResultSet rs)throws SQLException {
        Biglietto biglietto = new Biglietto();
        biglietto.setId(rs.getLong("id"));
        biglietto.setId_utente(rs.getLong("id_utente"));
        biglietto.setId_tratta(rs.getLong("id_tratta"));
        biglietto.setDataAquisto(rs.getTimestamp("data_acquisto"));
        biglietto.setDataConvalida(rs.getTimestamp("data_convalida"));
        biglietto.setStato(Biglietto.StatoBiglietto.valueOf(rs.getString("stato")));
        return  biglietto;

    }

    public static List<Biglietto> findByUserId(Long id) throws SQLException{
        String sql = "SELECT * FROM Biglietto WHERE id_utente = ?";
        try (Connection conn = DBConnector.getConnection()){
            List<Biglietto> biglietti = new ArrayList<Biglietto>();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                biglietti.add(getBfromSet(rs));
            }
            return biglietti;
            }
        }

    public static List<Biglietto> findByTrattaId(Long id) throws SQLException{
        String sql = "SELECT * FROM Biglietto WHERE id_tratta = ?";
        try (Connection conn = DBConnector.getConnection()){
            List<Biglietto> biglietti = new ArrayList<Biglietto>();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                biglietti.add(getBfromSet(rs));
            }
            return biglietti;
        }
    }

// CREATE
public static boolean create(Biglietto nuovoBiglietto) {
    String QRstr = "INSERT INTO Biglietto (data_acquisto, data_convalida, stato, id_utente, id_tratta) " +
            "VALUES (?,?,?,?,?) ";
    try (Connection con = DBConnector.getConnection()) {
        PreparedStatement ps = con.prepareStatement(QRstr);
        ps.setTimestamp(1, nuovoBiglietto.getDataAquisto());
        ps.setTimestamp(2, nuovoBiglietto.getDataConvalida());
        ps.setString(3, nuovoBiglietto.getStato().name());
        ps.setLong(4, nuovoBiglietto.getId_utente());
        ps.setLong(5, nuovoBiglietto.getId_tratta());

        return ps.executeUpdate() >= 1;

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}

// UPDATE
public static boolean update(Biglietto bigliettoInSessione) throws SQLException {
    String sql = "UPDATE Biglietto " +
            "SET data_acquisto=?, data_convalida=?, stato=?, id_utente=?, id_tratta=?" +
            " WHERE id=?";
    try (Connection conn = DBConnector.getConnection()) {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, bigliettoInSessione.getDataAquisto());
        ps.setTimestamp(2, bigliettoInSessione.getDataConvalida());
        ps.setString(3, bigliettoInSessione.getStato().name());
        ps.setLong(4, bigliettoInSessione.getId_utente());
        ps.setLong(5, bigliettoInSessione.getId_tratta());
        ps.setLong(6, bigliettoInSessione.getId());

        return ps.executeUpdate() >= 1;
    }
}

// FINDBYID
public static Biglietto findById(Long id) throws SQLException {
    try (Connection conn = DBConnector.getConnection()) {
        String sql = "SELECT * FROM Biglietto WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return getBfromSet(rs);
        }
        return null;
    }
}
}
