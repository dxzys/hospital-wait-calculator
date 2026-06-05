package main;
import functions.HospitalData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

 class Hospital {
    private String name;
    private double latitude;
    private double longitude;   
    private double distance;
    private double waitTime;

    public Hospital(String name, double latitude, double longitude, double distance, double waitTime) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.waitTime = waitTime;

        com.google.gson.JsonArray data = com.google.gson.JsonParser.parseString(functions.HospitalData.loadHospitalData())
                                                                   .getAsJsonObject()
                                                                   .getAsJsonArray("features");
        
        com.google.gson.JsonArray features = data.getAsJsonArray();                                                           
                            
        for (com.google.gson.JsonElement elements : features) {  
            com.google.gson.JsonObject feature = elements.getAsJsonObject();
            com.google.gson.JsonObject attributes = feature.getAsJsonObject().getAsJsonObject("attributes");
            com.google.gson.JsonObject geometry = feature.getAsJsonObject().getAsJsonObject("geometry");

            this.name = attributes.get("hospital_name").getAsString();

            this.waitTime = "N/A";
            if (attributes.has("ed_wait_range") && !attributes.get("ed_wait_range").isJsonNull()) {
                this.waitTime = attributes.get("ed_wait_range").getAsString();
            }

            double longitude = geometry.get("x").getAsDouble();
            double latitude = geometry.get("y").getAsDouble();
        }                                                         
    }
 }
