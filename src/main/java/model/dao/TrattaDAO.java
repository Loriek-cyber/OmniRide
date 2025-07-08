package model.dao;
import model.db.DBConnector;
import model.sdata.*;
import model.udata.Azienda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrattaDAO {
    private static final String TRATTA_ALL = "SELECT * FROM Tratta WHERE attiva = 1";
    private static final String TRATTA_BY_ID = "SELECT * FROM Tratta WHERE id = ?";
    private static final String TRATTA_BY_AZIENDA = "SELECT * FROM Tratta WHERE id_azienda = ? AND attiva = 1";
    private static final String INSERT_TRATTA = "INSERT INTO Tratta (nome, id_azienda, costo, attiva) VALUES (?, ?, ?, 1)";
    private static final String UPDATE_TRATTA = "UPDATE Tratta SET nome = ?, costo = ?, attiva = ? WHERE id = ?";
    private static final String DELETE_TRATTA = "UPDATE Tratta SET attiva = 0 WHERE id = ?";

    private static Tratta getTrattaFromResultSet(ResultSet rs) throws SQLException {
        Long trattaId = rs.getLong("id");
        Azienda azienda = AziendaDAO.findAziendaById(rs.getLong("id_azienda"));
        List<FermataTratta> fermataTrattaList = FermataTrattaDAO.findFermateByTrattaId(trattaId);
        List<OrarioTratta> orariTratta = OrarioTrattaDAO.findOrariByTrattaId(trattaId);
        
        Tratta tratta = new Tratta(
                trattaId,
                rs.getString("nome"),
                azienda,
                null, 
                fermataTrattaList,
                rs.getDouble("costo")
        );
        
        tratta.setOrari(orariTratta);
        tratta.setAttiva(rs.getBoolean("attiva"));
        
        return tratta;
    }

    public static Tratta findById(Long id) throws SQLException {
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
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_TRATTA, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nuovaTratta.getNome());
            ps.setLong(2, nuovaTratta.getAzienda().getId());
            ps.setDouble(3, nuovaTratta.getCosto());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creazione tratta fallita, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creazione tratta fallita, nessun ID generato.");
                }
            }
        }
    }
    
    public static boolean updateTratta(Tratta trattaInSessione) throws SQLException {
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_TRATTA)) {
            ps.setString(1, trattaInSessione.getNome());
            ps.setDouble(2, trattaInSessione.getCosto());
            ps.setBoolean(3, trattaInSessione.isAttiva());
            ps.setLong(4, trattaInSessione.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    public static List<Tratta> getAllTratte() throws SQLException {
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
    
    public static boolean deleteTratta(Long id) throws SQLException {
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_TRATTA)) {
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


}
