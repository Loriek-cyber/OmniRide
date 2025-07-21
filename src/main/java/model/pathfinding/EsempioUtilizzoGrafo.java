package model.pathfinding;

import model.dao.*;
import model.sdata.*;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;

//fucking funziona, bitch

public class EsempioUtilizzoGrafo {
    public static void main(String[] args) throws SQLException {
        List<Tratta> tratte = TrattaDAO.getAll();
        Fermata f1 =  FermataDAO.getById(6L);
        Fermata f2 =  FermataDAO.getById(1L);
        List<Fermata> fermataList = FermataDAO.getAll();
        Percorso percorso = Pathfinder.find(f1,f2,tratte,LocalTime.now().minusHours(2));
        if(percorso==null) {
            System.out.println("Percorso non trovato");
            return;
        }
        percorso.getSegmenti().forEach(segmenti -> {
            System.out.println(segmenti.getId_tratta());
            System.out.print(segmenti.getFermataIn().getNome()+"->"+segmenti.getFermataOu().getNome());
            System.out.println();
            System.out.println("Partenza:"+segmenti.getTempo_partenza());
            System.out.println("Arrivo:"+segmenti.getTempo_arrivo());
            System.out.println();


        });
        System.out.println(percorso.getCosto());
    }
}
