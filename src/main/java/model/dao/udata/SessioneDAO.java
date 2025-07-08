package model.dao.udata;
import model.dao.UtenteDAO;
import model.db.DBConnector;
import model.udata.Sessione;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.SecureRandom;
import java.util.Base64;


public class SessioneDAO {
    public static void salvaSessione(Sessione sessione) throws SQLException {
        String sql = "INSERT INTO sessioni (session_id, utente_id, creation_time, last_access_time, is_valid) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessione.getSessionId());
            stmt.setLong(2, sessione.getUtente().getId());
            stmt.setLong(3, sessione.getCreationTime());
            stmt.setLong(4, sessione.getLastAccessTime());
            stmt.setBoolean(5, sessione.isValid());
            stmt.executeUpdate();
        }
    }

    public static Sessione getSessioneBySessionId(String sessionId) throws SQLException {
        String sql = "SELECT * FROM sessioni WHERE session_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return getSessioneFromSet(rs);
                }
            }
        }
        return null;
    }

    public static void invalidaSessione(String sessionId) throws SQLException {
        String sql = "UPDATE sessioni SET is_valid = false WHERE session_id = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.executeUpdate();
        }
    }
    
    public static void aggiornaUltimoAccesso(String sessionId, long lastAccessTime) throws SQLException {
        String sql = "UPDATE sessioni SET last_access_time = ? WHERE session_id = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, lastAccessTime);
            stmt.setString(2, sessionId);
            stmt.executeUpdate();
        }
    }
    
    public static boolean sessioneEsistente(String sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sessioni WHERE session_id = ? AND is_valid = true";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private static Sessione getSessioneFromSet(ResultSet rs)throws SQLException{
        Sessione sessione = new Sessione();
        sessione.setSessionId(rs.getString("session_id"));
        sessione.setUtente(UtenteDAO.getById(rs.getLong("utente_id")));
        sessione.setCreationTime(rs.getLong("creation_time"));
        sessione.setLastAccessTime(rs.getLong("last_access_time"));
        sessione.setValid(rs.getBoolean("is_valid"));
        return sessione;
    }

}