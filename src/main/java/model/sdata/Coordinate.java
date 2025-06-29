package model.sdata;
import java.util.Objects;

public class Coordinate {
    private double latitudine;
    private double longitudine;

    public Coordinate() {}

    public Coordinate(double latitudine, double longitudine) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    // Calcolo distanza haversine in km
    public double distanzaDa(Coordinate altra) {
        final int R = 6371; // Raggio Terra in km

        double latDistance = Math.toRadians(altra.latitudine - this.latitudine);
        double lonDistance = Math.toRadians(altra.longitudine - this.longitudine);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitudine)) * Math.cos(Math.toRadians(altra.latitudine))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    // Getters e Setters
    public double getLatitudine() { return latitudine; }
    public void setLatitudine(double latitudine) { this.latitudine = latitudine; }
    public double getLongitudine() { return longitudine; }
    public void setLongitudine(double longitudine) { this.longitudine = longitudine; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinate that = (Coordinate) obj;
        return Double.compare(that.latitudine, latitudine) == 0 &&
                Double.compare(that.longitudine, longitudine) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitudine, longitudine);
    }
}