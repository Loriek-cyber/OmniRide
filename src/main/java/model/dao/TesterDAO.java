package model.dao;

import model.sdata.FermataTratta;
import model.sdata.Tratta;
import model.sdata.UnicaTratta;
import model.udata.Azienda;
import model.udata.Utente;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class TesterDAO {
    //questa è la classe in cui testo i vari elementi del programma
    public static void main(String[] args) throws SQLException {
        Long id = 1L;
        Azienda azienda = AziendaDAO.getById(id);
        id = 2L;
        Utente utente = UtenteDAO.getById(id);

        DipendentiDAO.combine(azienda,utente,"GESTORE");
    }
}
