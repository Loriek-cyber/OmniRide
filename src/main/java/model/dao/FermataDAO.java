package model.dao;

import model.db.DBConnector;
import model.sdata.Coordinate;
import model.sdata.Fermata;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FermataDAO {

    /**
     * Estrae un oggetto Fermata da un ResultSet.
     * Gestisce la creazione dell'oggetto Coordinate e la conversione dell'enum TipoFermata.
     */
    private static Fermata extractFermataFromResultSet(ResultSet rs) throws SQLException {
        Fermata fermata = new Fermata();
        fermata.setId(rs.getLong("id"));
        fermata.setNome(rs.getString("nome"));
        fermata.setIndirizzo(rs.getString("indirizzo"));
        fermata.setCoordinate(new Coordinate(rs.getDouble("latitudine"), rs.getDouble("longitudine")));
        
        String tipoStr = rs.getString("tipo");
        if (tipoStr != null && !tipoStr.isEmpty()) {
            try {
                // Uso toUpperCase() per rendere il matching dell'enum case-insensitive
                fermata.setTipo(Fermata.TipoFermata.valueOf(tipoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Se il tipo nel DB non corrisponde a nessun valore dell'enum,
                // imposto un default per evitare crash dell'applicazione.
                fermata.setTipo(Fermata.TipoFermata.FERMATA_NORMALE);
            }
        } else {
            fermata.setTipo(Fermata.TipoFermata.FERMATA_NORMALE);
        }
        
        fermata.setAttiva(rs.getBoolean("attiva"));
        return fermata;
    }

    /**
     * Inserisce una nuova fermata nel database.
     * NOTA: Le coordinate (latitudine/longitudine) devono essere già presenti nell'oggetto.
     * La logica di geocoding (conversione indirizzo -> coordinate) deve avvenire a un livello superiore
     * (es. nel Servlet) prima di chiamare questo metodo.
     *
     * @param nuovaFermata L'oggetto Fermata da creare, con coordinate già impostate.
     * @return L'ID della nuova fermata creata.
     * @throws SQLException in caso di errore del database.
     */
    public static Long create(Fermata nuovaFermata) throws SQLException {
        String sql = "INSERT INTO Fermata (nome, indirizzo, latitudine, longitudine, tipo, attiva) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nuovaFermata.getNome());
            ps.setString(2, nuovaFermata.getIndirizzo());
            ps.setDouble(3, nuovaFermata.getCoordinate().getLatitudine());
            ps.setDouble(4, nuovaFermata.getCoordinate().getLongitudine());
            ps.setString(5, nuovaFermata.getTipo().name());
            ps.setBoolean(6, nuovaFermata.isAttiva());

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Creazione fermata fallita, nessuna riga modificata.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creazione fermata fallita, nessun ID ottenuto.");
                }
            }
        }
    }

    /**
     * Aggiorna i dati di una fermata esistente.
     *
     * @param FermataInSessione L'oggetto Fermata con i dati aggiornati.
     * @return true se l'aggiornamento ha avuto successo.
     * @throws SQLException in caso di errore del database.
     */
    public static boolean update(Fermata FermataInSessione) throws SQLException {
        String sql = "UPDATE Fermata SET nome = ?, indirizzo = ?, latitudine = ?, longitudine = ?, tipo = ?, attiva = ? WHERE id = ?";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, FermataInSessione.getNome());
            ps.setString(2, FermataInSessione.getIndirizzo());
            ps.setDouble(3, FermataInSessione.getCoordinate().getLatitudine());
            ps.setDouble(4, FermataInSessione.getCoordinate().getLongitudine());
            ps.setString(5, FermataInSessione.getTipo().name());
            ps.setBoolean(6, FermataInSessione.isAttiva());
            ps.setLong(7, FermataInSessione.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Trova una fermata specifica tramite il suo ID.
     *
     * @param id L'ID della fermata da cercare.
     * @return Un oggetto Fermata se trovato, altrimenti null.
     * @throws SQLException in caso di errore del database.
     */
    public static Fermata getById(Long id) throws SQLException {
        String sql = "SELECT * FROM Fermata WHERE id = ?";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFermataFromResultSet(rs);
                }
                return null;
            }
        }
    }

    /**
     * Recupera tutte le fermate presenti nel database.
     *
     * @return Una lista di oggetti Fermata.
     * @throws SQLException in caso di errore del database.
     */
    public static List<Fermata> doRetrieveAll() throws SQLException {
        List<Fermata> fermate = new ArrayList<>();
        String sql = "SELECT * FROM Fermata";
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    fermate.add(extractFermataFromResultSet(rs));
                }
            }
            return fermate;
        }
    }
}