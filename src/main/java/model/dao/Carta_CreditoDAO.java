package model.dao;

import model.db.DBConnector;
import model.udata.CartaCredito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Carta_CreditoDAO {
    public static Long create(CartaCredito cartaCredito) throws SQLException {
        if (cartaCredito == null) {
            throw new IllegalArgumentException("CartaCredito non può essere null");
        }

        final String sql = "INSERT INTO Carte_Credito (id_utente, nome_intestatario, numero_carta, data_scadenza, cvv) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, cartaCredito.getId_utente());
            ps.setString(2, cartaCredito.getNome_intestatario());
            ps.setString(3, cartaCredito.getNumeroCarta());
            ps.setString(4, cartaCredito.getData_scadenza());
            ps.setString(5, cartaCredito.getCvv());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Inserimento fallito, nessuna riga inserita");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Inserimento fallito, ID non generato");
                }
            }
        }
    }

    private static CartaCredito getFromSet(ResultSet rs) throws SQLException {
        CartaCredito cartaCredito = new CartaCredito();
        cartaCredito.setNome_intestatario(rs.getString("nome_intestatario"));
        cartaCredito.setNumeroCarta(rs.getString("numero_carta"));
        cartaCredito.setCvv(rs.getString("cvv"));
        cartaCredito.setData_scadenza(rs.getString("data_scadenza"));
        cartaCredito.setId_utente(rs.getLong("id_utente"));
        return  cartaCredito;
    }

    public static List<CartaCredito> byIDUtente(long id) throws SQLException {
        String sql = "SELECT * FROM Carte_Credito WHERE id_utente = ?";
        List<CartaCredito> cartaCredito = new ArrayList<>();
        try(Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                cartaCredito.add(getFromSet(rs));
            }
            return  cartaCredito;
        }
    }
}
