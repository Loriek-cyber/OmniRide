package model.DAO;
import model.sdata.Fermata;
import model.sdata.FermataTratta;
import model.db.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
