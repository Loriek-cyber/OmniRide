package testing;
import data.Fermata;
import data.Tratta;
import java.util.ArrayList;

public class testingDijkstras {
	
	public static void main(String[] args) {
		Fermata f1 = new Fermata("Fermata 1", "1", "45.123", "9.123");
		Fermata f2 = new Fermata("Fermata 2", "2", "45.124", "9.124");
		Fermata f3 = new Fermata("Fermata 3", "3", "45.125", "9.125");
		
		Tratta tratta = new Tratta("T1", "Tratta 1", new ArrayList<Fermata>());
		tratta.addFermata(f1);
		tratta.addFermata(f2);
		tratta.addFermata(f3);
		
		System.out.println("Tratta ID: " + tratta.getId());
		System.out.println("Tratta Nome: " + tratta.getNome());
		
	}

}
