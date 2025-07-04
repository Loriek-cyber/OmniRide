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
    private  static Utente getUtenteFromSet(ResultSet rs) throws  SQLException {
        Utente utente;
        try{
            utente = new Utente();

        }
    }
    public static Utente findByEmail(String email) {}
}
