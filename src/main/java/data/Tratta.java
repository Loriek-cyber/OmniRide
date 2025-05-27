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
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tratta [id=").append(id).append(", nome=").append(nome).append(", fermate=");
		for (Fermata f : fermate) {
			sb.append(f.toString()).append(", ");
		}
		sb.append("]");
		return sb.toString();
	}
}
