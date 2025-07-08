package model.dao;

import model.db.DBConnector;
import model.udata.Azienda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AziendaDAO {

    /**
     * Estrae un oggetto Azienda da un ResultSet.
     * Metodo helper privato per evitare duplicazione di codice.
     *
     * @param rs Il ResultSet da cui estrarre i dati.
     * @return Un oggetto Azienda popolato.
     * @throws SQLException in caso di errore di accesso ai dati del ResultSet.
     */
    private static Azienda extractAziendaFromResultSet(ResultSet rs) throws SQLException {
        Azienda azienda = new Azienda();
        azienda.setId(rs.getLong("id"));
        azienda.setNome(rs.getString("nome"));
        azienda.setTipo(rs.getString("tipo"));
        return azienda;
    }

    /**
     * Inserisce una nuova azienda nel database.
     *
     * @param nuovoAzienda L'oggetto Azienda con i dati da inserire.
     * @return L'ID generato per la nuova azienda.
     * @throws SQLException in caso di errore del database.
     */
    public static long create(Azienda nuovoAzienda) throws SQLException {
        String sql = "INSERT INTO Azienda (nome, tipo) VALUES (?, ?)";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, nuovoAzienda.getNome());
            ps.setString(2, nuovoAzienda.getTipo());
            
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Creazione azienda fallita, nessuna riga modificata.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creazione azienda fallita, nessun ID ottenuto.");
                }
            }
        }
    }

    /**
     * Aggiorna i dati di un'azienda esistente nel database.
     *
     * @param AziendaInSessione L'oggetto Azienda con l'ID dell'azienda da aggiornare e i nuovi dati.
     * @return true se l'aggiornamento ha modificato almeno una riga, false altrimenti.
     * @throws SQLException in caso di errore del database.
     */
    public static boolean update(Azienda AziendaInSessione) throws SQLException {
        String sql = "UPDATE Azienda SET nome = ?, tipo = ? WHERE id = ?";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, AziendaInSessione.getNome());
            ps.setString(2, AziendaInSessione.getTipo());
            ps.setLong(3, AziendaInSessione.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Trova un'azienda specifica tramite il suo ID.
     *
     * @param id L'ID dell'azienda da cercare.
     * @return Un oggetto Azienda se trovato, altrimenti null.
     * @throws SQLException in caso di errore del database.
     */
    public static Azienda getById(Long id) throws SQLException {
        String sql = "SELECT * FROM Azienda WHERE id = ?";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractAziendaFromResultSet(rs);
                }
            }
            return null;
        }
    }

    /**
     * Recupera tutte le aziende presenti nel database.
     *
     * @return Una lista di oggetti Azienda.
     * @throws SQLException in caso di errore del database.
     */
    public static List<Azienda> getAll() throws SQLException {
        String sql = "SELECT * FROM Azienda";
        List<Azienda> aziende = new ArrayList<>();
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                aziende.add(extractAziendaFromResultSet(rs));
            }
            return aziende;
        }
    }

    /**
     * Elimina un'azienda dal database.
     *
     * @param id L'ID dell'azienda da eliminare.
     * @return true se l'eliminazione ha avuto successo, false altrimenti.
     * @throws SQLException in caso di errore del database.
     */
    public static boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM Azienda WHERE id = ?";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Alias per compatibilità con codice esistente.
     * @deprecated Usare getAll() invece
     */
    @Deprecated
    public static List<Azienda> doRetrieveAll() throws SQLException {
        return getAll();
    }
}
