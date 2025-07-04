package model.dao;

import model.sdata.FermataTratta;
import model.sdata.Tratta;
import model.sdata.UnicaTratta;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class TesterDAO {
    //questa è la classe in cui testo i vari elementi del programma
    public static void main(String[] args) throws SQLException {
        List<Tratta> allTratte = TrattaDAO.getAllTratte();
        for (Tratta tratta : allTratte) {
            System.out.println(tratta.getNome());

            for(UnicaTratta unica : tratta.getUnicaTrattaList()){
                for(FermataTratta fermata : tratta.getFermataTrattaList()){
                    System.out.print(fermata.getFermata().getNome());
                    System.out.print(fermata.getFermata().getIndirizzo());
                    
                }
            }
        }
    }
}
