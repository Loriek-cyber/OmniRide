package model.dao;

import model.db.DBConnector;
import model.udata.Biglietto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BigliettiDAO {

    /**
     * Estrae un oggetto Biglietto da un ResultSet.
     */
    private static Biglietto extractBigliettoFromResultSet(ResultSet rs) throws SQLException {
        Biglietto biglietto = new Biglietto();
        biglietto.setId(rs.getLong("id"));
        biglietto.setIdUtente(rs.getLong("id_utente"));
        biglietto.setIdTratta(rs.getLong("id_tratta"));
        biglietto.setIdOrario(rs.getLong("id_orario"));
        if (rs.wasNull()) {
            biglietto.setIdOrario(null);
        }
        biglietto.setDataAcquisto(rs.getTimestamp("data_acquisto"));
        biglietto.setDataConvalida(rs.getTimestamp("data_convalida"));
        biglietto.setDataScadenza(rs.getTimestamp("data_scadenza"));
        biglietto.setPrezzoPagato(rs.getBigDecimal("prezzo_pagato"));
        biglietto.setStato(Biglietto.StatoBiglietto.valueOf(rs.getString("stato")));
        return biglietto;
    }

    /**
     * Crea un nuovo biglietto nel database (es. a seguito di un acquisto).
     *
     * @param nuovoBiglietto L'oggetto Biglietto da creare.
     * @return L'ID del biglietto appena creato.
     * @throws SQLException in caso di errore del database.
     */
    public static Long create(Biglietto nuovoBiglietto) throws SQLException {
        String sql = "INSERT INTO biglietto (id_utente, id_tratta, id_orario, data_acquisto, stato, prezzo_pagato) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, nuovoBiglietto.getIdUtente());
            ps.setLong(2, nuovoBiglietto.getIdTratta());
            if (nuovoBiglietto.getIdOrario() != null) {
                ps.setLong(3, nuovoBiglietto.getIdOrario());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setString(5, Biglietto.StatoBiglietto.ACQUISTATO.name());
            ps.setBigDecimal(6, nuovoBiglietto.getPrezzoPagato());

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Creazione biglietto fallita, nessuna riga modificata.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creazione biglietto fallita, nessun ID ottenuto.");
                }
            }
        }
    }

    /**
     * Aggiorna lo stato e le date di un biglietto esistente.
     *
     * @param BigliettoInSessione L'oggetto Biglietto con i dati aggiornati.
     * @return true se l'aggiornamento ha avuto successo.
     * @throws SQLException in caso di errore del database.
     */
    public static boolean update(Biglietto BigliettoInSessione) throws SQLException {
        String sql = "UPDATE biglietto SET data_convalida = ?, data_scadenza = ?, stato = ? WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, BigliettoInSessione.getDataConvalida());
            ps.setTimestamp(2, BigliettoInSessione.getDataScadenza());
            ps.setString(3, BigliettoInSessione.getStato().name());
            ps.setLong(4, BigliettoInSessione.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Trova un biglietto specifico tramite il suo ID.
     *
     * @param id L'ID del biglietto da cercare.
     * @return Un oggetto Biglietto se trovato, altrimenti null.
     * @throws SQLException in caso di errore del database.
     */
    public static Biglietto getById(Long id) throws SQLException {
        String sql = "SELECT * FROM biglietto WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBigliettoFromResultSet(rs);
                }
            }
            return null;
        }
    }

    /**
     * Recupera tutti i biglietti di un utente specifico.
     *
     * @param idUtente L'ID dell'utente.
     * @return Una lista di oggetti Biglietto.
     * @throws SQLException in caso di errore del database.
     */
    public static List<Biglietto> findBigliettiByUtente(Long idUtente) throws SQLException {
        String sql = "SELECT * FROM biglietto WHERE id_utente = ? ORDER BY data_acquisto DESC";
        List<Biglietto> biglietti = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    biglietti.add(extractBigliettoFromResultSet(rs));
                }
            }
        }
        return biglietti;
    }
}