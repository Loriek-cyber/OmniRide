package data;
import java.util.ArrayList;

public class Tratta {
	
	private String id;
	private String nome;
	private ArrayList<Fermata> fermate;
	public Tratta(String id, String nome, ArrayList<Fermata> fermate) {
		this.id = id;
		this.nome = nome;
		this.fermate = fermate;
	}
	
	public String getId() {
		return id;
	}
	
	public String getNome() {
		return nome;
	}

	public void addFermata(Fermata fermata) {
		this.fermate.add(fermata);
	}
	
	public ArrayList<Fermata> getFermate() {
		return fermate;
	}
	
}
