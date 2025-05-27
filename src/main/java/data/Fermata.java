package data;

public class Fermata {
	
	private String nome;
	private String id;
	private String latitudine;
	private String longitudine;

	public Fermata(String nome, String id, String latitudine, String longitudine) {
		this.nome = nome;
		this.id = id;
		this.latitudine = latitudine;
		this.longitudine = longitudine;
	}

	public String getNome() {
		return nome;
	}

	public String getId() {
		return id;
	}

	public String getLatitudine() {
		return latitudine;
	}

	public String getLongitudine() {
		return longitudine;
	}
	
	public String toString() {
		return "Fermata [nome=" + nome + ", id=" + id + ", latitudine=" + latitudine + ", longitudine=" + longitudine + "]";
	}
}
