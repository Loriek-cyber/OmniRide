package model.dao;

import model.db.DBConnector;
import model.udata.CartaCredito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Carta_CreditoDAO {
    private static CartaCredito getFromSet(ResultSet rs) throws SQLException {
        CartaCredito cartaCredito = new CartaCredito();
        cartaCredito.setNome_intestatario(rs.getString("nome_intestatario"));
        cartaCredito.setNumeroCarta(rs.getString("numero_carta"));
        cartaCredito.setCvv(rs.getString("cvv"));
        cartaCredito.setData_scadenza(rs.getString("data_scadenza"));
        cartaCredito.setId_utente(rs.getLong("id_utente"));
        return  cartaCredito;
    }


    public static CartaCredito byIDUtente() throws SQLException {
        String sql = "SELECT * FROM carta_credito WHERE id_utente = ?";

        try(Connection conn = DBConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            return getFromSet(rs);
        }
    }
}
