package data;

import java.util.ArrayList;

public class Tratta {
    
    private String idTratta;
    private ArrayList<Fermata> fermate;
    
    public Tratta(String idTratta, ArrayList<Fermata> fermate) {
        this.idTratta = idTratta;
        // Se la lista passata è null, inizializzo una nuova lista
        this.fermate = (fermate != null) ? fermate : new ArrayList<>();
    }
    
    public String getIdTratta() {
        return idTratta;
    }
    
    public ArrayList<Fermata> getFermate() {
        return fermate;
    }
    
    public void addFermata(Fermata fermata) {
        if (fermate == null) {
            fermate = new ArrayList<>();
        }
        fermate.add(fermata);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tratta [idTratta=").append(idTratta).append(", fermate=");
        for (Fermata fermata : fermate) {
            sb.append(fermata.toString()).append("; ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    // Metodo shortcut per ottenere l'ID della tratta
    public String getId() {
        return idTratta;
    }
}