package model.dao;

import model.sdata.FermataTratta;
import model.sdata.Tratta;
import model.udata.Azienda;
import model.udata.Utente;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class TesterDAO {
    //questa è la classe in cui testo i vari elementi del programma
    public static void main(String[] args) throws SQLException {
        TrattaDAO.deleteTratta(1L);
    }
}
