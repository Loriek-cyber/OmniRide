package model.dao;

import model.db.DBConnector;
import model.sdata.Avvisi;

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

    public static Avvisi getAvvisiById(Long id) throws SQLException {
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement pr = conn.prepareStatement("SELECT * FROM Avviso WHERE id = ?");
            pr.setLong(1, id);
            ResultSet rs = pr.executeQuery();
            if(rs.next()){
                return getAvvisiFromSet(rs);
            }
            return null;
        }
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

}
