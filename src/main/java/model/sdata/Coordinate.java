package model.sdata;

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

    // Funzione per verificare se le coordinate sono valide
    public boolean isValid() {
        return Latitudine >= -90 && Latitudine <= 90 && 
               Longitude >= -180 && Longitude <= 180;
    }

    // Funzione per calcolare la distanza tra due coordinate (formula di Haversine)
    public double distanceTo(Coordinate other) {
        final int R = 6371; // Raggio della Terra in km
        
        double latDistance = Math.toRadians(other.Latitudine - this.Latitudine);
        double lonDistance = Math.toRadians(other.Longitude - this.Longitude);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.Latitudine)) * Math.cos(Math.toRadians(other.Latitudine))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distanza in km
    }

    // Funzione per creare una copia della coordinata
    public Coordinate copy() {
        return new Coordinate(this.Latitudine, this.Longitude);
    }

    // Funzione per convertire in formato stringa leggibile
    public String toReadableString() {
        String latDir = Latitudine >= 0 ? "N" : "S";
        String lonDir = Longitude >= 0 ? "E" : "W";
        
        return String.format("%.6f°%s, %.6f°%s", 
                           Math.abs(Latitudine), latDir, 
                           Math.abs(Longitude), lonDir);
    }

    // Funzione per convertire in formato DMS (Degrees, Minutes, Seconds)
    public String toDMSString() {
        return String.format("%s, %s", 
                           convertToDMS(Latitudine, true), 
                           convertToDMS(Longitude, false));
    }

    private String convertToDMS(double decimal, boolean isLatitude) {
        String direction;
        if (isLatitude) {
            direction = decimal >= 0 ? "N" : "S";
        } else {
            direction = decimal >= 0 ? "E" : "W";
        }
        
        decimal = Math.abs(decimal);
        int degrees = (int) decimal;
        decimal = (decimal - degrees) * 60;
        int minutes = (int) decimal;
        double seconds = (decimal - minutes) * 60;
        
        return String.format("%d°%d'%.2f\"%s", degrees, minutes, seconds, direction);
    }

    // Funzione per verificare se la coordinata è nell'emisfero nord
    public boolean isNorthernHemisphere() {
        return Latitudine >= 0;
    }

    // Funzione per verificare se la coordinata è nell'emisfero est
    public boolean isEasternHemisphere() {
        return Longitude >= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Coordinate that = (Coordinate) obj;
        return Double.compare(that.Latitudine, Latitudine) == 0 &&
               Double.compare(that.Longitude, Longitude) == 0;
    }

    @Override
    public int hashCode() {
        long latBits = Double.doubleToLongBits(Latitudine);
        long lonBits = Double.doubleToLongBits(Longitude);
        return (int) (latBits ^ (latBits >>> 32) ^ lonBits ^ (lonBits >>> 32));
    }

    @Override
    public String toString() {
        return String.format("Coordinate{Latitudine=%.6f, Longitude=%.6f}", 
                           Latitudine, Longitude);
    }
}