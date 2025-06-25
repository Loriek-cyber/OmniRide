package net.data;

public class Coordinate {
    private double Latitudine;
    private double Longitude;

    public Coordinate(double lat, double lon) {
        this.Latitudine = lat;
        this.Longitude = lon;
    }

    public double getLatitudine() {
        return Latitudine;
    }

    public void setLatitudine(double latitudine) {
        Latitudine = latitudine;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
