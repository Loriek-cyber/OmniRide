package model.dao;
import model.db.DBConnector;
import model.udata.Utente;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Blob;

public class UtenteDAO {
    private  static Utente getUtenteFromSet(ResultSet rs) throws SQLException{
        Utente utente;
        utente = new Utente();
        utente.setId(rs.getLong("id"));
        utente.setNome(rs.getString("nome"));
        utente.setCognome(rs.getString("cognome"));
        utente.setEmail(rs.getString("email"));
        utente.setDataRegistrazione(rs.getTimestamp("dataRegistrazione"));
        utente.setRuolo(rs.getString("ruolo"));
        utente.setAvatar(rs.getBytes("avatar"));
        return utente;
    }


    public static Utente findByEmail(String email)  {
        String QRsql="SELECT * FROM Utente WHERE email=?";
        try(Connection con=DBConnector.getConnection()){
            //preaprato la query
            PreparedStatement ps = con.prepareStatement(QRsql);
            ps.setString(1,email);
            //eseguito la quety
            ResultSet rs = ps.executeQuery();
            /*in poche parole
            * 1) la query dice che colonna trovare
            * 2) ps.string(1, email) dice che alla primo punto interrogativo della query deve inserire l'email passata come stringa, questo permete di trovare l'utente e di stampare l'intera riga*/
            return getUtenteFromSet(rs);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //FINDBYID
    public static Utente findById(Long id) throws SQLException{
        String QRstr="SELECT * FROM Utente WHERE id=?";
        try(Connection con=DBConnector.getConnection()){
          PreparedStatement ps=con.prepareStatement(QRstr);
          ps.setLong(1, id);
          ResultSet rs=ps.executeQuery();
          return getUtenteFromSet(rs);
        }
    }

    public static List<Utente> getAllUtenti() throws SQLException{
        String QRstr="SELECT * FROM Utente";
        try(Connection con=DBConnector.getConnection()){
            PreparedStatement ps=con.prepareStatement(QRstr);
            ResultSet rs=ps.executeQuery();
            List<Utente> Utenti=new ArrayList<>();
            while(rs.next()){
                Utenti.add(getUtenteFromSet(rs));
            }
            return Utenti;
        }
    }

    //CREATE
    public static boolean create(Utente nuovoUtente) {
        String QRstr="INSERT TO Utente (nome, cognome, email, password_hash, data_registrazione, ruolo, avatar) " +
                "VALUES (?,?,?,?,?,?,?) ";
        try(Connection con=DBConnector.getConnection()){
            PreparedStatement ps=con.prepareStatement(QRstr);
            ps.setString(1, nuovoUtente.getNome());
            ps.setString(2, nuovoUtente.getCognome());
            ps.setString(3, nuovoUtente.getEmail());
            ps.setString(4, nuovoUtente.getPasswordHash());
            ps.setTimestamp(5, nuovoUtente.getDataRegistrazione());
            ps.setString(6, nuovoUtente.getRuolo());
            ps.setBytes(7, nuovoUtente.getAvatar());

            if(ps.executeUpdate()>=1){
                return true;
            }else return false;

        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public static byte[] getAvatarByUserId(Long userId) throws SQLException{
        return findById(userId).getAvatar();
    }

    //UPDATE
    public static boolean update(Utente utenteInSessione) throws SQLException {
        String sql = "UPDATE Utente " +
                "SET nome=?,cognome=?,email=?,password_hash=?,data_registrazione,ruolo=?,avatar=?" +
                " WHERE id=?";
        try (Connection conn = DBConnector.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(8,utenteInSessione.getId());

            ps.setString(1, utenteInSessione.getNome());
            ps.setString(2, utenteInSessione.getCognome());
            ps.setString(3, utenteInSessione.getEmail());
            ps.setString(4, utenteInSessione.getPasswordHash());
            ps.setTimestamp(5, utenteInSessione.getDataRegistrazione());
            ps.setString(6, utenteInSessione.getRuolo());
            ps.setBytes(7, utenteInSessione.getAvatar());

            if(ps.executeUpdate()>=1){
                return true;
            }else return false;
        }

    }
}
