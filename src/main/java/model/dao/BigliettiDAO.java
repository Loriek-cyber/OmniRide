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

    public static Biglietto GetBigliettoFromID(Long id) throws  SQLException {
        try(Connection conn = DBConnector.getConnection()){
            String sql = "SELECT * FROM Biglietto WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return getBfromSet(rs);
            }
            return null;
        }
    }

    public static List<Biglietto> GetBigliettoFromUserID(Long id) throws SQLException{
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

    public static List<Biglietto> GetBigliettoFromTrattaID(Long id) throws SQLException{
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


}


