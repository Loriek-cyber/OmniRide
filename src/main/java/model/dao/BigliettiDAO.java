package model.dao;
import model.db.DBConnector;
import model.udata.Biglietto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BigliettiDAO {

    public static void delete(Biglietto biglietto) {

    }

    public static void update(Biglietto biglietto) {

    }

    public static void insert(Biglietto biglietto) {

    }

    public static Long create(Biglietto biglietto) throws SQLException {
        String sql = "INSERT INTO Biglietto (id_utente, prezzo, data_acquisto, data_convalida, data_fine, stato) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setObject(1, biglietto.getId_utente());
            ps.setDouble(2, biglietto.getPrezzo());
            ps.setObject(3, biglietto.getDataAcquisto());
            ps.setObject(4, biglietto.getDataConvalida());
            ps.setObject(5, biglietto.getDataFine());
            ps.setString(6, biglietto.getStato().toString());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Inserimento biglietto fallito, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Inserimento biglietto fallito, nessun ID generato.");
                }
            }
        }
    }
}

