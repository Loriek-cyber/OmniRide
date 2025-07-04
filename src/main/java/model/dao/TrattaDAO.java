package model.dao;
import model.db.DBConnector;
import model.sdata.*;
import model.udata.Azienda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object per la gestione delle tratte.
 * Gestisce le operazioni CRUD per le tratte con la nuova struttura semplificata degli orari.
 */
public class TrattaDAO {
    private static final String TRATTA_ALL = "SELECT * FROM Tratta WHERE attiva = 1";
    private static final String TRATTA_BY_ID = "SELECT * FROM Tratta WHERE id = ?";
    private static final String TRATTA_BY_AZIENDA = "SELECT * FROM Tratta WHERE id_azienda = ? AND attiva = 1";
    private static final String INSERT_TRATTA = "INSERT INTO Tratta (nome, id_azienda, costo, attiva) VALUES (?, ?, ?, 1)";
    private static final String UPDATE_TRATTA = "UPDATE Tratta SET nome = ?, costo = ?, attiva = ? WHERE id = ?";
    private static final String DELETE_TRATTA = "UPDATE Tratta SET attiva = 0 WHERE id = ?";

    /**
     * Estrae una tratta completa dal ResultSet includendo fermate e orari
     */
    private static Tratta getTrattaFromResultSet(ResultSet rs) throws SQLException {
        Long trattaId = rs.getLong("id");
        
        // Carica l'azienda
        Azienda azienda = AziendaDAO.doRetrieveById(rs.getLong("id_azienda"));
        
        // Carica le fermate della tratta
        List<FermataTratta> fermataTrattaList = FermataTrattaDAO.getFTfromTrattaID(trattaId);
        
        // Carica gli orari della tratta (nuova struttura)
        List<OrarioTratta> orariTratta = OrarioTrattaDAO.getOrariByTrattaId(trattaId);
        
        // Crea la tratta
        Tratta tratta = new Tratta(
                trattaId,
                rs.getString("nome"),
                azienda,
                null, // unicaTrattaList non più necessaria
                fermataTrattaList,
                rs.getDouble("costo")
        );
        
        // Imposta gli orari
        tratta.setOrari(orariTratta);
        tratta.setAttiva(rs.getBoolean("attiva"));
        
        return tratta;
    }

    /**
     * Recupera una tratta per ID
     */
    public static Tratta getTrattaByID(Long id) throws SQLException {
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement ps = conn.prepareStatement(TRATTA_BY_ID);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getTrattaFromResultSet(rs);
            }
            return null;
        }
    }

    /**
     * Recupera tutte le tratte attive
     */
    public static List<Tratta> getAllTratte() throws SQLException {
        List<Tratta> tratte = new ArrayList<>();
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(TRATTA_ALL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tratte.add(getTrattaFromResultSet(rs));
            }
            return tratte;
        }
    }
    
    /**
     * Recupera le tratte di una specifica azienda
     */
    public static List<Tratta> getTratteByCzienda(Long idAzienda) throws SQLException {
        List<Tratta> tratte = new ArrayList<>();
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(TRATTA_BY_AZIENDA);
            ps.setLong(1, idAzienda);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tratte.add(getTrattaFromResultSet(rs));
            }
            return tratte;
        }
    }
    
    /**
     * Inserisce una nuova tratta
     */
    public static Long insertTratta(Tratta tratta) throws SQLException {
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(INSERT_TRATTA, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tratta.getNome());
            ps.setLong(2, tratta.getAzienda().getId());
            ps.setDouble(3, tratta.getCosto());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserimento tratta fallito, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Inserimento tratta fallito, nessun ID generato.");
                }
            }
        }
    }
    
    /**
     * Aggiorna una tratta esistente
     */
    public static boolean updateTratta(Tratta tratta) throws SQLException {
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(UPDATE_TRATTA);
            ps.setString(1, tratta.getNome());
            ps.setDouble(2, tratta.getCosto());
            ps.setBoolean(3, tratta.isAttiva());
            ps.setLong(4, tratta.getId());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Elimina logicamente una tratta (la disattiva)
     */
    public static boolean deleteTratta(Long id) throws SQLException {
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(DELETE_TRATTA);
            ps.setLong(1, id);
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Recupera solo il nome di una tratta per ID
     */
    public static String getTrattaNameByID(Long id) throws SQLException {
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(TRATTA_BY_ID);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nome");
            }
        }
        return null;
    }
    
    /**
     * Calcola il tempo totale di percorrenza di una tratta
     */
    public static int calcolaTempoTotalePercorrenza(Long trattaId) throws SQLException {
        List<FermataTratta> fermate = FermataTrattaDAO.getFTfromTrattaID(trattaId);
        return fermate.stream()
                .mapToInt(FermataTratta::getTempoProssimaFermata)
                .sum();
    }
}