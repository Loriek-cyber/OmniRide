package model.dao;

import model.db.DBConnector;
import model.sdata.Avvisi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvvisiDAO {

    private static Avvisi getAvvisiFromSet(ResultSet rs) throws SQLException {
        Avvisi avvisi = new Avvisi();
        avvisi.setId(rs.getLong("id"));
        avvisi.setDescrizione(rs.getString("descrizione"));
        List<Long> data = new ArrayList<>();
        try(Connection conn = DBConnector.getConnection();
            PreparedStatement pr = conn.prepareStatement("SELECT * FROM Avvisi_tratte WHERE avviso_id = ?");) {
            pr.setLong(1, avvisi.getId());
            try(ResultSet rs2 = pr.executeQuery()){
                while(rs2.next()){
                    data.add(rs2.getLong("tratta_id"));
                }
            }
            avvisi.setId_tratte_coinvolte(data);
        }
        return avvisi;
        }


    public static List<Avvisi> getAll() throws SQLException {
        List<Avvisi> avvisi = new ArrayList<>();
        try(Connection conn = DBConnector.getConnection();
            PreparedStatement pr = conn.prepareStatement("SELECT * FROM Avvisi");
            ResultSet rs = pr.executeQuery()){
            while(rs.next()){
                avvisi.add(getAvvisiFromSet(rs));
            }
        }
        return avvisi;
    }

    public static List<Avvisi> getAvvisiByTrattaId(Long id) throws SQLException {
        List<Avvisi> avvisi = new ArrayList<>();
        String sql = "SELECT a.* " +
                "FROM Avvisi a " +
                "JOIN Avvisi_tratte at ON a.id = at.avviso_id " +
                "WHERE at.tratta_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pr = conn.prepareStatement(sql)){
            pr.setLong(1, id);
            try(ResultSet rs = pr.executeQuery()){
                while(rs.next()){
                    avvisi.add(getAvvisiFromSet(rs));
                }
            }
            return avvisi;
        }
    }

    public static Long create(Avvisi nuovoAvvisi) throws SQLException {
        String insertAvvisoSQL = "INSERT INTO Avvisi (descrizione, data_inizio, data_fine, tipo) VALUES (?, ?, ?, ?)";
        String insertAvvisiTratteSQL = "INSERT INTO Avvisi_tratte (avviso_id, tratta_id) VALUES (?, ?)";
        Long avvisoId = null;

        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement psAvviso = conn.prepareStatement(insertAvvisoSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psAvviso.setString(1, nuovoAvvisi.getDescrizione());
                
                psAvviso.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                psAvviso.setTimestamp(3, null);
                psAvviso.setString(4, "INFO"); 

                if (psAvviso.executeUpdate() == 0) {
                    throw new SQLException("Creazione avviso fallita, nessuna riga modificata.");
                }

                try (ResultSet generatedKeys = psAvviso.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        avvisoId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Creazione avviso fallita, nessun ID ottenuto.");
                    }
                }
            }

            if (nuovoAvvisi.getId_tratte_coinvolte() != null && !nuovoAvvisi.getId_tratte_coinvolte().isEmpty()) {
                try (PreparedStatement psAvvisiTratte = conn.prepareStatement(insertAvvisiTratteSQL)) {
                    for (Long trattaId : nuovoAvvisi.getId_tratte_coinvolte()) {
                        psAvvisiTratte.setLong(1, avvisoId);
                        psAvvisiTratte.setLong(2, trattaId);
                        psAvvisiTratte.addBatch();
                    }
                    psAvvisiTratte.executeBatch();
                }
            }

            conn.commit(); 
            return avvisoId;

        }
    }

    public static boolean update(Avvisi AvvisiInSessione) throws SQLException {
        String updateAvvisoSQL = "UPDATE Avvisi SET descrizione = ?, data_inizio = ?, data_fine = ?, tipo = ? WHERE id = ?";
        String deleteTratteSQL = "DELETE FROM Avvisi_tratte WHERE avviso_id = ?";
        String insertTratteSQL = "INSERT INTO Avvisi_tratte (avviso_id, tratta_id) VALUES (?, ?)";

        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement psUpdate = conn.prepareStatement(updateAvvisoSQL)) {
                psUpdate.setString(1, AvvisiInSessione.getDescrizione());
                
                psUpdate.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                psUpdate.setTimestamp(3, null);
                psUpdate.setString(4, "INFO");

                psUpdate.setLong(5, AvvisiInSessione.getId());
                psUpdate.executeUpdate();
            }

            try (PreparedStatement psDelete = conn.prepareStatement(deleteTratteSQL)) {
                psDelete.setLong(1, AvvisiInSessione.getId());
                psDelete.executeUpdate();
            }

            if (AvvisiInSessione.getId_tratte_coinvolte() != null && !AvvisiInSessione.getId_tratte_coinvolte().isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertTratteSQL)) {
                    for (Long trattaId : AvvisiInSessione.getId_tratte_coinvolte()) {
                        psInsert.setLong(1, AvvisiInSessione.getId());
                        psInsert.setLong(2, trattaId);
                        psInsert.addBatch();
                    }
                    psInsert.executeBatch();
                }
            }

            conn.commit(); 
            return true;

        }
    }

    public static Avvisi getById(Long id) throws SQLException {
        try(Connection conn = DBConnector.getConnection();
            PreparedStatement pr = conn.prepareStatement("SELECT * FROM Avvisi WHERE id = ?")){
            pr.setLong(1, id);
            try(ResultSet rs = pr.executeQuery()){
                if(rs.next()){
                    return getAvvisiFromSet(rs);
                }
            }
            return null;
        }
    }

    public static boolean delete(Long id) throws SQLException {
        String deleteAvvisiTratteSQL = "DELETE FROM Avvisi_tratte WHERE avviso_id = ?";
        String deleteAvvisoSQL = "DELETE FROM Avvisi WHERE id = ?";
        
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);
            
            // Prima elimina le relazioni con le tratte
            try (PreparedStatement psTratte = conn.prepareStatement(deleteAvvisiTratteSQL)) {
                psTratte.setLong(1, id);
                psTratte.executeUpdate();
            }
            
            // Poi elimina l'avviso
            try (PreparedStatement psAvviso = conn.prepareStatement(deleteAvvisoSQL)) {
                psAvviso.setLong(1, id);
                int rowsAffected = psAvviso.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            }
            
        }
    }
}
