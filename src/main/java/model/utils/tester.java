package model.utils;

import model.dao.FermataTrattaDAO;
import model.dao.OrarioTrattaDAO;
import model.dao.TrattaDAO;
import model.sdata.Fermata;
import model.sdata.FermataTratta;
import model.sdata.OrarioTratta;
import model.sdata.Tratta;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class tester {
    public static void main(String[] args) {
        System.out.println(Fermata.TipoFermata.valueOf("EXTRAURBANA"));
    }}


