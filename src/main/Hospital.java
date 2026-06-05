package main;

public class Hospital {
    public String name;
    public double latitude, longitude, distance;
    public String waitTime;
    public Hospital(String name, double latitude, double longitude, double distance, String waitTime) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.waitTime = waitTime;

        com.google.gson.JsonObject data = com.google.gson.JsonParser.parseString(functions.HospitalData.loadHospitalData()).getAsJsonObject();
        com.google.gson.JsonArray features = data.getAsJsonArray("features");

        java.lang.String closestHospitalName = "";
        double minDistance = java.lang.Double.MAX_VALUE;

        for (com.google.gson.JsonElement elements : features) {
            com.google.gson.JsonObject feature = elements.getAsJsonObject();
            com.google.gson.JsonObject attributes = feature.getAsJsonObject("attributes");
            com.google.gson.JsonObject geometry = feature.getAsJsonObject("geometry");

            java.lang.String hospitalName = attributes.get("hospital_name").getAsString();
            double hLat = geometry.get("y").getAsDouble();
            double hLon = geometry.get("x").getAsDouble();

            double dist = functions.Commute.getDistance(main.Main.userLocation[0], main.Main.userLocation[1], hLat, hLon);

            if (dist < minDistance) {
                minDistance = dist;
                closestHospitalName = hospitalName;
            }
        }
        java.lang.System.out.format("\n[Result] closest: %s | distance: %.2f km\n\n", closestHospitalName, minDistance);
    }
                                  
    
}
