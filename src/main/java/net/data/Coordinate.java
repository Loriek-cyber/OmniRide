package net.data;

public class Coordinate {
	
	private double longitudine;
	private double latitudine;
	
	public Coordinate(double longitudine, double latitudine) {
		this.latitudine= latitudine;
		this.longitudine = longitudine;
	}

	public double getLongitudine() {
		return longitudine;
	}

	public void setLongitudine(double longitudine) {
		this.longitudine = longitudine;
	}

	public double getLatitudine() {
		return latitudine;
	}

	public void setLatitudine(double latitudine) {
		this.latitudine = latitudine;
	}

	@Override
	public String toString() {
		return "Coordinate [longitudine=" + longitudine + ", latitudine=" + latitudine + "]";
	}
	
	
}
