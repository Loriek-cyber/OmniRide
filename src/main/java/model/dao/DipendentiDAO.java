package model.dao;
import model.udata.Azienda;
import model.udata.Dipendenti;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DipendentiDAO{
    private Dipendenti getDipendentifromSet(ResultSet rs) throws SQLException{
        Dipendenti dipendente = new Dipendenti();
        try {
            dipendente.setAzienda(AziendaDAO.doRetrieveById(rs.getLong("id_azienda")));
            dipendente.setLavoro(Dipendenti.Lavoro.valueOf(rs.getString("lavoro")));

        } finally {

        }
        return null;
    }
}