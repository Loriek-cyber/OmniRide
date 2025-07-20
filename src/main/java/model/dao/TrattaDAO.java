package model.dao;
import model.db.DBConnector;
import model.sdata.*;
import model.udata.Azienda;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TrattaDAO {
    private static final String TRATTA_ALL = "SELECT * FROM Tratta WHERE attiva = 1";
    private static final String TRATTA_BY_ID = "SELECT * FROM Tratta WHERE id = ?";
    private static final String TRATTA_BY_AZIENDA = "SELECT * FROM Tratta WHERE id_azienda = ?";
    private static final String INSERT_TRATTA = "INSERT INTO Tratta (nome, id_azienda, costo, attiva) VALUES (?, ?, ?, 1)";
    private static final String UPDATE_TRATTA = "UPDATE Tratta SET nome = ?, costo = ?, attiva = ? WHERE id = ?";
    private static final String NOACTIVE = "UPDATE Tratta SET attiva = 0 WHERE id = ?";
    private static final String ACTIVE = "UPDATE Tratta SET attiva = 1 WHERE id = ?";
    private static final String AZIENDA = "SELECT * FROM Tratta WHERE id_azienda = ?";

    private static Tratta getTrattaFromResultSet(ResultSet rs) throws SQLException {
        Long trattaId = rs.getLong("id");
        Azienda azienda = AziendaDAO.getById(rs.getLong("id_azienda"));
        List<FermataTratta> fermataTrattaList = FermataTrattaDAO.findFermateByTrattaId(trattaId);
        List<OrarioTratta> orariTratta = OrarioTrattaDAO.findOrariByTrattaId(trattaId);
        double costo = rs.getDouble("costo");
        Tratta tratta = new Tratta(trattaId,rs.getString("nome"),azienda,fermataTrattaList,orariTratta,costo);
        tratta.setAttiva(rs.getBoolean("attiva"));
        return tratta;
    }

    public static boolean activate(Long id_tratta) throws SQLException {
        try (Connection con = DBConnector.getConnection();){
            PreparedStatement ps = con.prepareStatement(ACTIVE);
            ps.setLong(1, id_tratta);
            ps.executeUpdate();
            return true;
        }
    }

    public static Tratta getById(Long id) throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(TRATTA_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getTrattaFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public static Long create(Tratta nuovaTratta) throws SQLException {
        Connection con = null;
        Long id_tratta = null;
        
        try {
            con = DBConnector.getConnection();
            // Inizia la transazione
            con.setAutoCommit(false);
            
            // Crea la tratta principale
            try (PreparedStatement ps = con.prepareStatement(INSERT_TRATTA, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nuovaTratta.getNome());
                ps.setLong(2, nuovaTratta.getAzienda().getId());
                ps.setDouble(3, nuovaTratta.getCosto());
                
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creazione tratta fallita, nessuna riga modificata.");
                }
                
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        id_tratta = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Creazione tratta fallita, nessun ID generato.");
                    }
                }
            }
            
            // Crea le fermate della tratta
            for(FermataTratta fermataTratta : nuovaTratta.getFermataTrattaList()) {
                fermataTratta.setIdTratta(id_tratta);
                FermataTrattaDAO.createWithConnection(fermataTratta, con);
            }
            
            // Crea gli orari della tratta
            for(OrarioTratta orarioTratta : nuovaTratta.getOrari()) {
                orarioTratta.setTrattaId(id_tratta);
                OrarioTrattaDAO.createWithConnection(orarioTratta, con);
            }
            
            // Se tutto è andato bene, conferma la transazione
            con.commit();
            System.out.println("[TRATTA_CREATE] Tratta creata con successo con ID: " + id_tratta);
            
            return id_tratta;
            
        } catch (SQLException e) {
            // In caso di errore, annulla la transazione
            System.err.println("[TRATTA_CREATE ERROR] Errore durante la creazione della tratta: " + e.getMessage());
            e.printStackTrace();
            
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("[TRATTA_CREATE] Rollback completato per tratta ID: " + id_tratta);
                } catch (SQLException rollbackEx) {
                    System.err.println("[TRATTA_CREATE ERROR] Errore durante il rollback: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            
            throw new SQLException("Errore durante la creazione della tratta: " + e.getMessage(), e);
            
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Ripristina l'autocommit
                    con.close();
                } catch (SQLException e) {
                    System.err.println("[TRATTA_CREATE ERROR] Errore durante la chiusura della connessione: " + e.getMessage());
                }
            }
        }
    }


    public static List<Tratta> findByPartenzaArrivoDataOrario(String partenza, String arrivo, LocalDate data, LocalTime orario) throws SQLException {
        List<Tratta> tratte = new ArrayList<>();
        // Ottieni il giorno della settimana in formato abbreviato (es. LUN, MAR, ecc.)
        String giorno = data.getDayOfWeek().toString().substring(0, 3).toUpperCase();
        if (giorno.equals("MON")) giorno = "LUN";
        else if (giorno.equals("TUE")) giorno = "MAR";
        else if (giorno.equals("WED")) giorno = "MER";
        else if (giorno.equals("THU")) giorno = "GIO";
        else if (giorno.equals("FRI")) giorno = "VEN";
        else if (giorno.equals("SAT")) giorno = "SAB";
        else if (giorno.equals("SUN")) giorno = "DOM";
        
        String query = "SELECT DISTINCT t.* FROM Tratta t " +
                       "JOIN Tratta_Orari ot ON t.id = ot.id_tratta " +
                       "JOIN Fermata_Tratta ft1 ON t.id = ft1.id_tratta " +
                       "JOIN Fermata f1 ON ft1.id_fermata = f1.id " +
                       "JOIN Fermata_Tratta ft2 ON t.id = ft2.id_tratta " +
                       "JOIN Fermata f2 ON ft2.id_fermata = f2.id " +
                       "WHERE f1.nome = ? AND f2.nome = ? " +
                       "AND ot.ora_partenza >= ? " +
                       "AND ot.giorni_settimana LIKE ? " +
                       "AND ft1.sequenza < ft2.sequenza AND t.attiva = 1 AND ot.attivo = 1";

        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, partenza);
            ps.setString(2, arrivo);
            ps.setTime(3, Time.valueOf(orario));
            ps.setString(4, "%" + giorno + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tratte.add(getTrattaFromResultSet(rs));
                }
            }
        }
        return tratte;
    }

    public static boolean update(Tratta trattaInSessione) throws SQLException {
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_TRATTA)) {
            ps.setString(1, trattaInSessione.getNome());
            ps.setDouble(2, trattaInSessione.getCosto());
            ps.setBoolean(3, trattaInSessione.isAttiva());
            ps.setLong(4, trattaInSessione.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    public static List<Tratta> getAll() throws SQLException {
        List<Tratta> tratte = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(TRATTA_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tratte.add(getTrattaFromResultSet(rs));
            }
        }
        return tratte;
    }
    
    public static List<Tratta> getTratteByAzienda(Long idAzienda) throws SQLException {
        List<Tratta> tratte = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(TRATTA_BY_AZIENDA)) {
            ps.setLong(1, idAzienda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tratte.add(getTrattaFromResultSet(rs));
                }
            }
        }
        return tratte;
    }
    
    public static boolean deativate(Long id) throws SQLException {
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(NOACTIVE)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static String getTrattaNameByID(Long id) throws SQLException {
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(TRATTA_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nome");
                }
            }
        }
        return null;
    }
    
    public static int calcolaTempoTotalePercorrenza(Long trattaId) throws SQLException {
        List<FermataTratta> fermate = FermataTrattaDAO.findFermateByTrattaId(trattaId);
        return fermate.stream()
                .mapToInt(FermataTratta::getTempoProssimaFermata)
                .sum();
    }

    public static List<Tratta> getAzienda(Long id) throws SQLException {
        List<Tratta> tratte = new ArrayList<>();
        try(Connection con = DBConnector.getConnection()){
            PreparedStatement ps = con.prepareStatement(AZIENDA);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tratte.add(getTrattaFromResultSet(rs));
            }
        }
        return tratte;
    }

    // Metodi per compatibilità con codice esistente
    @Deprecated
    public static List<Tratta> getAllTratte() throws SQLException {
        return getAll();
    }
    
    @Deprecated
    public static Tratta doRetrieveById(Long id) throws SQLException {
        return getById(id);
    }
    
    @Deprecated
    public static List<Tratta> doRetrieveAll() throws SQLException {
        return getAll();
    }

    public static void delete(Long routeId) {
        return;
    }
}
