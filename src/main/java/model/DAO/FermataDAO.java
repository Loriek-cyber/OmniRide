package model.DAO;
import model.db.DBConnector;
import model.sdata.Fermata;
import model.sdata.Coordinate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FermataDAO {
    public Fermata getFermata(Long id) throws SQLException {
        String query = "SELECT * FROM Fermata WHERE id = ?";
        try(Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();){
            while(rs.next()){
                Fermata fermata = new Fermata();
                fermata.setId(rs.getLong("id"));
                fermata.setNome(rs.getString("nome"));
                fermata.setCoordinate(new Coordinate(rs.getDouble("latitudine"), rs.getDouble("longitudine")));
                fermata.setTipo(Fermata.TipoFermata.valueOf(rs.getString("tipo")));
                fermata.setAttiva(rs.getBoolean("attiva"));
                return fermata;
            }
        }
        throw new SQLException("Errore nella lettura dei dati");
    }


    public ArrayList<Fermata> getFermate() throws SQLException {
        String query = "SELECT * FROM Fermata";
        ArrayList<Fermata> fermate;
        try(Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();){
            fermate = new ArrayList<>();
            while(rs.next()){
                Fermata fermata = new Fermata();
                fermata.setId(rs.getLong("id"));
                fermata.setNome(rs.getString("nome"));
                fermata.setCoordinate(new Coordinate(rs.getDouble("latitudine"), rs.getDouble("longitudine")));
                fermata.setTipo(Fermata.TipoFermata.valueOf(rs.getString("tipo")));
                fermata.setAttiva(rs.getBoolean("attiva"));
                fermate.add(fermata);
            }
            }
        throw new SQLException("Errore nella lettura dei dati");
    }

    public void addFermata(Fermata fermata) throws SQLException {
        String query = "INSERT INTO Fermata (nome, latitudine, longitudine, tipo, attiva) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = DBConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);){
            stmt.setString(1, fermata.getNome());
            stmt.setDouble(2, fermata.getCoordinate().getLatitudine());
            stmt.setDouble(3, fermata.getCoordinate().getLongitudine());
            stmt.setString(4, fermata.getTipo().toString());
            }
    }
}