package model.dao;
import model.sdata.FermataTratta;
import model.db.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FermataTrattaDAO {
    private static final String getFTfromTid = "SELECT * FROM Fermata_Tratta WHERE id_tratta = ? ORDER BY sequenza";

    private static FermataTratta getFTfromSet(ResultSet rs) throws SQLException {
        return new FermataTratta(
                rs.getLong("id_tratta"),
                FermataDAO.doRetrieveById(rs.getLong("id_fermata")),
                null, // La prossima fermata verrà impostata dopo
                rs.getInt("tempo_prossima_fermata")
        );
    }

    public static List<FermataTratta> getFTfromTrattaID(long id_tratta) throws SQLException {
        List<FermataTratta> lft = new ArrayList<>();
        try(Connection conn = DBConnector.getConnection()){
            PreparedStatement ps = conn.prepareStatement(getFTfromTid);
            ps.setLong(1, id_tratta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                lft.add(getFTfromSet(rs));
            }
        }

        // Imposta la fermata successiva per ogni elemento della lista
        if (!lft.isEmpty()) {
            for (int i = 0; i < lft.size() - 1; i++) {
                lft.get(i).setProssimaFermata(lft.get(i + 1).getFermata());
            }
            // L'ultima fermata non ha una prossima fermata, quindi impostiamo a null esplicitamente
            lft.get(lft.size() - 1).setProssimaFermata(null);
        }

        return lft;
    }

    /**
     * Inserisce una nuova relazione fermata-tratta nel database
     * @param fermataTratta La relazione da inserire
     * @return L'ID della relazione inserita, null se l'inserimento fallisce
     * @throws SQLException Se c'è un errore nel database
     */
    public static Long insertFermataTratta(FermataTratta fermataTratta) throws SQLException {
        String sql = "INSERT INTO Fermata_Tratta (id_tratta, id_fermata, tempo_prossima_fermata, sequenza) VALUES (?, ?, ?, ?)";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, fermataTratta.getIdTratta());
            ps.setLong(2, fermataTratta.getFermata().getId());
            ps.setInt(3, fermataTratta.getTempoProssimaFermata());
            
            // Calcola la sequenza automaticamente
            int sequenza = getNextSequenza(fermataTratta.getIdTratta());
            ps.setInt(4, sequenza);
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Inserimento fermata-tratta fallito, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Inserimento fermata-tratta fallito, nessun ID generato.");
                }
            }
        }
    }

    /**
     * Calcola la prossima sequenza per una tratta
     * @param idTratta L'ID della tratta
     * @return Il numero di sequenza successivo
     * @throws SQLException Se c'è un errore nel database
     */
    private static int getNextSequenza(Long idTratta) throws SQLException {
        String sql = "SELECT COALESCE(MAX(sequenza), 0) + 1 as next_seq FROM Fermata_Tratta WHERE id_tratta = ?";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, idTratta);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_seq");
                }
                return 1; // Prima fermata
            }
        }
    }

    /**
     * Elimina tutte le relazioni fermata-tratta per una specifica tratta
     * @param idTratta L'ID della tratta
     * @return true se l'eliminazione è riuscita, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean deleteFermateByTratta(Long idTratta) throws SQLException {
        String sql = "DELETE FROM Fermata_Tratta WHERE id_tratta = ?";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, idTratta);
            return ps.executeUpdate() >= 0;
        }
    }

    /**
     * Aggiorna una relazione fermata-tratta esistente
     * @param fermataTratta La relazione da aggiornare
     * @return true se l'aggiornamento è riuscito, false altrimenti
     * @throws SQLException Se c'è un errore nel database
     */
    public static boolean updateFermataTratta(FermataTratta fermataTratta, int sequenza) throws SQLException {
        String sql = "UPDATE Fermata_Tratta SET tempo_prossima_fermata = ? WHERE id_tratta = ? AND id_fermata = ? AND sequenza = ?";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, fermataTratta.getTempoProssimaFermata());
            ps.setLong(2, fermataTratta.getIdTratta());
            ps.setLong(3, fermataTratta.getFermata().getId());
            ps.setInt(4, sequenza);
            
            return ps.executeUpdate() > 0;
        }
    }
}
