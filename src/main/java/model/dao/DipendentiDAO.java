package model.dao;

import model.db.DBConnector;
import model.udata.Azienda;
import model.udata.Dipendenti;
import model.udata.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DipendentiDAO {

    /**
     * Estrae un oggetto Dipendenti da un ResultSet.
     * Popola anche gli oggetti Utente e Azienda associati.
     */
    private static Dipendenti extractDipendenteFromResultSet(ResultSet rs) throws SQLException {
        Dipendenti dipendente = new Dipendenti();
        
        // Carica l'utente e l'azienda usando i rispettivi DAO
        dipendente.setUtente(UtenteDAO.getById(rs.getLong("id_utente")));
        dipendente.setAzienda(AziendaDAO.getById(rs.getLong("id_azienda")));
        dipendente.setLavoro(Dipendenti.Lavoro.valueOf(rs.getString("ruolo")));
        dipendente.setAttivo(rs.getBoolean("attivo"));
        
        return dipendente;
    }

    /**
     * Crea un nuovo legame Dipendente nel database.
     *
     * @param nuovoDipendente L'oggetto Dipendenti contenente Utente, Azienda e ruolo.
     * @return true se l'inserimento ha avuto successo.
     * @throws SQLException in caso di errore del database.
     */
    public static boolean create(Dipendenti nuovoDipendente) throws SQLException {
        String sql = "INSERT INTO Dipendente (id_utente, id_azienda, ruolo, attivo) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, nuovoDipendente.getUtente().getId());
            ps.setLong(2, nuovoDipendente.getAzienda().getId());
            ps.setString(3, nuovoDipendente.getLavoro().name());
            ps.setBoolean(4, nuovoDipendente.isAttivo());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * @param azienda azienda di interesse con il quale associare l'utente
     * @param utente, utente che sarà assunto dall'azienda
     * @throws SQLException
     */

    public static void combine(Azienda azienda, Utente utente, String lavoro) throws SQLException {

        Dipendenti  dipendente = new Dipendenti();
        dipendente.setUtente(utente);
        dipendente.setLavoro(Dipendenti.Lavoro.valueOf(lavoro.toUpperCase()));
        dipendente.setAzienda(azienda);
        try (Connection con = DBConnector.getConnection()) {
            create(dipendente);
        }
    }

    public static void adminconbine(Long id_utente,Long id_azienda) throws SQLException {
        String sql = "INSERT INTO Dipendente (id_utente, id_azienda, ruolo, attivo) VALUES (?, ?, ?, ?)";
        try(Connection conn = DBConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, id_utente);
            ps.setLong(2, id_azienda);
            ps.setString(3,"GESTORE");
            ps.setBoolean(4, true);
            ps.executeUpdate();
        }
    }

    /**
     * Aggiorna il ruolo o lo stato di un dipendente.
     *
     * @param DipendenteInSessione L'oggetto con i dati da aggiornare.
     * @return true se l'aggiornamento ha avuto successo.
     * @throws SQLException in caso di errore del database.
     */
    public static boolean update(Dipendenti DipendenteInSessione) throws SQLException {
        String sql = "UPDATE Dipendente SET ruolo = ?, attivo = ? WHERE id_utente = ? AND id_azienda = ?";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, DipendenteInSessione.getLavoro().name());
            ps.setBoolean(2, DipendenteInSessione.isAttivo());
            ps.setLong(3, DipendenteInSessione.getUtente().getId());
            ps.setLong(4, DipendenteInSessione.getAzienda().getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Trova un dipendente specifico usando la sua chiave primaria composta.
     *
     * @param idUtente  L'ID dell'utente.
     * @param idAzienda L'ID dell'azienda.
     * @return Un oggetto Dipendenti se trovato, altrimenti null.
     * @throws SQLException in caso di errore del database.
     */
    public static Dipendenti getById(Long idUtente, Long idAzienda) throws SQLException {
        String sql = "SELECT * FROM Dipendente WHERE id_utente = ? AND id_azienda = ?";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ps.setLong(2, idAzienda);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractDipendenteFromResultSet(rs);
                }
            }
            return null;
        }
    }

    /**
     * Recupera tutti i dipendenti di una specifica azienda.
     *
     * @param idAzienda L'ID dell'azienda.
     * @return Una lista di oggetti Dipendenti.
     * @throws SQLException in caso di errore del database.
     */
    public static List<Dipendenti> findDipendentiByAzienda(Long idAzienda) throws SQLException {
        String sql = "SELECT * FROM Dipendente WHERE id_azienda = ? AND attivo = 1";
        List<Dipendenti> dipendenti = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, idAzienda);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dipendenti.add(extractDipendenteFromResultSet(rs));
                }
            }
        }
        return dipendenti;
    }

    /**
     * Elimina un dipendente dal database.
     *
     * @param idUtente L'ID dell'utente.
     * @param idAzienda L'ID dell'azienda.
     * @return true se l'eliminazione ha avuto successo.
     * @throws SQLException in caso di errore del database.
     */
    public static boolean delete(Long idUtente, Long idAzienda) throws SQLException {
        String sql = "DELETE FROM Dipendente WHERE id_utente = ? AND id_azienda = ?";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ps.setLong(2, idAzienda);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Recupera tutti i dipendenti del database.
     *
     * @return Una lista di oggetti Dipendenti.
     * @throws SQLException in caso di errore del database.
     */
    public static List<Dipendenti> getAll() throws SQLException {
        String sql = "SELECT * FROM Dipendente";
        List<Dipendenti> dipendenti = new ArrayList<>();
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dipendenti.add(extractDipendenteFromResultSet(rs));
                }
            }
        }
        return dipendenti;
    }


}
