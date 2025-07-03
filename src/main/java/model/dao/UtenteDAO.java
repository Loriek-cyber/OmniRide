package model.dao;
import model.db.DBConnector;
import model.udata.Utente;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Blob;

public class UtenteDAO {

    public Utente findByEmail(String email) {
        String sql = "SELECT * FROM Utente WHERE email = ?";
        Utente utente = null;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    utente = new Utente();
                    utente.setId(rs.getLong("id"));
                    utente.setNome(rs.getString("nome"));
                    utente.setCognome(rs.getString("cognome"));
                    utente.setEmail(rs.getString("email"));
                    utente.setPasswordHash(rs.getString("password_hash"));
                    utente.setDataRegistrazione(rs.getTimestamp("data_registrazione"));
                    utente.setRuolo(rs.getString("ruolo"));
                }
            }
        } catch (SQLException e) {
            // In un'applicazione reale, qui si dovrebbe loggare l'errore
            e.printStackTrace();
        }
        return utente;
    }

    /**
     * Crea un nuovo utente nel database.
     * La password viene hashata con BCrypt prima di essere salvata.
     * @param utente L'oggetto Utente da creare. La password deve essere in chiaro.
     * @return true se la creazione ha avuto successo, false altrimenti.
     */
    public boolean create(Utente utente) {
        String sql = "INSERT INTO Utente (nome, cognome, email, password_hash, ruolo) VALUES (?, ?, ?, ?, ?)";

        // Genera il salt e hasha la password
        String hashedPassword = BCrypt.hashpw(utente.getPasswordHash(), BCrypt.gensalt());

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getEmail());
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, "utente"); // Ruolo di default

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Utente> findAll() throws SQLException {
        String sql = "SELECT * FROM Utente";
        List<Utente> utenti = new ArrayList<>();
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement pt = conn.prepareStatement(sql);
            ResultSet rs = pt.executeQuery();
            while (rs.next()){
                utenti.add(getUtenteFromSet(rs));
            }
            return utenti;
        }
    }

    public byte[] getAvatarByUserId(int userId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        byte[] avatarData = null;

        try {
            connection = DBConnector.getConnection();
            String sql = "SELECT avatar FROM Utente WHERE id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                Blob blob = rs.getBlob("avatar");
                if (blob != null) {
                    avatarData = blob.getBytes(1, (int) blob.length());
                }
            }
        } finally {
            // Chiudi le risorse
            if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException ignore) {}
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }
        return avatarData;
    }

    private Utente getUtenteFromSet(ResultSet rs) throws SQLException {
        Utente utente = new Utente();
        utente.setId(rs.getLong("id"));
        utente.setNome(rs.getString("nome"));
        utente.setCognome(rs.getString("cognome"));
        utente.setEmail(rs.getString("email"));
        utente.setPasswordHash(rs.getString("password_hash"));
        return  utente;
    }

    public boolean update(Utente utente) throws SQLException {
        String sql = "UPDATE Utente SET nome = ?, cognome = ?, email = ?, password_hash = ? WHERE id = ?";
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement pt = conn.prepareStatement(sql);
            pt.setString(1, utente.getNome());
            pt.setString(2, utente.getCognome());
            pt.setString(3, utente.getEmail());
            pt.setString(4, utente.getPasswordHash());
            pt.setLong(5, utente.getId());
            int affectedRows = pt.executeUpdate();
            return affectedRows > 0;
        }

    }
}
