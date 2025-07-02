package model.DAO;

import model.sdata.Tratta;

import java.sql.SQLException;
import java.util.List;

public class TesterDAO {
    //questa è la classe in cui testo i vari elementi del programma
    public static void main(String[] args) throws SQLException {
        List<Tratta> allTratte = TrattaDAO.getAllTratte();
        System.out.println(allTratte);
    }
}
