package net.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

public class GeoLock {

    public static Coordinate getCoordinatesFromAddress(String address) throws Exception {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("L'indirizzo non può essere nullo o vuoto.");
        }

        String encodedAddress = URLEncoder.encode(address, "UTF-8");
        String url = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress + "&format=json&limit=1";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "JavaGeocoder/1.0 (your.actual.email@example.com)"); // FIXME: Update with a valid email

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Errore nella richiesta HTTP: codice " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responseStr = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            responseStr.append(line);
        }
        in.close();

        JSONArray results = new JSONArray(responseStr.toString());
        if (results.isEmpty()) {
            throw new Exception("Indirizzo non trovato.");
        }

        JSONObject firstResult = results.getJSONObject(0);
        double lat = Double.parseDouble(firstResult.getString("lat"));
        double lon = Double.parseDouble(firstResult.getString("lon"));

        return new Coordinate(lon, lat); // Corrected order: longitude, then latitude
    }
}
