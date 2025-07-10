package model.pathfinding;

import model.dao.AvvisiDAO;
import model.sdata.*;
import model.udata.Azienda;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Test {

    public static void main(String[] args) throws SQLException {
        Avvisi avviso = new Avvisi();
        avviso.setDescrizione("C'è stato un'errore nelle gestione delle tratte");
        avviso.setId_tratte_coinvolte(new ArrayList<>());
        AvvisiDAO.create(avviso);
    }
}