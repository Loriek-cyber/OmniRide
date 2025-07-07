package model.dao;
import model.db.DBConnector;
import model.sdata.Coordinate;
import model.sdata.Fermata;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FermataDAO {

    public static Fermata doRetrieveById(long id) throws SQLException {
        try (Connection con = DBConnector.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Fermata WHERE id = ?");
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFermataFromResultSet(rs);
                }
                return null;
            }
        }
    }

    public static List<Fermata> doRetrieveAll() throws SQLException {
        List<Fermata> fermate = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Fermata");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Fermata fermata = extractFermataFromResultSet(rs);
                if (fermata != null) {
                    fermate.add(fermata);
                }
            }
            return fermate;
        }
    }

    private static Fermata extractFermataFromResultSet(ResultSet rs) throws SQLException {
        Fermata fermata = new Fermata();
        fermata.setId(rs.getLong("id"));
        fermata.setNome(rs.getString("nome"));
        fermata.setIndirizzo(rs.getString("indirizzo"));
        fermata.setCoordinate(new Coordinate(rs.getDouble("latitudine"), rs.getDouble("longitudine")));
        String tipoStr = rs.getString("tipo");
        if (tipoStr != null && !tipoStr.isEmpty()) {
            try {
                fermata.setTipo(Fermata.TipoFermata.valueOf(tipoStr));
            } catch (IllegalArgumentException e) {
                // Se il tipo non è valido, usa un valore di default
                fermata.setTipo(Fermata.TipoFermata.FERMATA_NORMALE);
            }
        } else {
            fermata.setTipo(Fermata.TipoFermata.FERMATA_NORMALE);
        }
        fermata.setAttiva(rs.getBoolean("attiva"));
        return fermata;
    }

    /**
     * Inserisce una nuova fermata nel database
     * @param fermata La fermata da inserire
     * @return L'ID della fermata inserita, null se l'inserimento fallisce
     * @throws SQLException Se c'è un errore nel database
     */
    public static Long insertFermata(Fermata fermata) throws SQLException {
        String sql = "INSERT INTO Fermata (nome, indirizzo, latitudine, longitudine, tipo, attiva) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, fermata.getNome());
            ps.setString(2, fermata.getIndirizzo());
            
            if (fermata.getCoordinate() != null) {
                ps.setDouble(3, fermata.getCoordinate().getLatitudine());
                ps.setDouble(4, fermata.getCoordinate().getLongitudine());
            } else {
                ps.setNull(3, Types.DOUBLE);
                ps.setNull(4, Types.DOUBLE);
            }
            
            ps.setString(5, fermata.getTipo() != null ? fermata.getTipo().name() : Fermata.TipoFermata.FERMATA_NORMALE.name());
            ps.setBoolean(6, fermata.isAttiva());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Inserimento fermata fallito, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Inserimento fermata fallito, nessun ID generato.");
                }
            }
        }
    }

    /**
     * Aggiorna una fermata esistente
     * @param fermata La fermata da aggiornare
     * @return true se l'aggiornamento è riuscito, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean updateFermata(Fermata fermata) throws SQLException {
        String sql = "UPDATE Fermata SET nome = ?, indirizzo = ?, latitudine = ?, longitudine = ?, tipo = ?, attiva = ? WHERE id = ?";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, fermata.getNome());
            ps.setString(2, fermata.getIndirizzo());
            
            if (fermata.getCoordinate() != null) {
                ps.setDouble(3, fermata.getCoordinate().getLatitudine());
                ps.setDouble(4, fermata.getCoordinate().getLongitudine());
            } else {
                ps.setNull(3, Types.DOUBLE);
                ps.setNull(4, Types.DOUBLE);
            }
            
            ps.setString(5, fermata.getTipo() != null ? fermata.getTipo().name() : Fermata.TipoFermata.FERMATA_NORMALE.name());
            ps.setBoolean(6, fermata.isAttiva());
            ps.setLong(7, fermata.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina (disattiva) una fermata
     * @param id L'ID della fermata da eliminare
     * @return true se l'eliminazione è riuscita, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean deleteFermata(Long id) throws SQLException {
        String sql = "UPDATE Fermata SET attiva = false WHERE id = ?";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Recupera solo le fermate attive
     * @return Lista delle fermate attive
     * @throws SQLException Se c'è un errore nel database
     */
    public static List<Fermata> doRetrieveActive() throws SQLException {
        List<Fermata> fermate = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Fermata WHERE attiva = true");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Fermata fermata = extractFermataFromResultSet(rs);
                if (fermata != null) {
                    fermate.add(fermata);
                }
            }
            return fermate;
        }
    }
}
