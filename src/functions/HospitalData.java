package functions;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HospitalData {
    public HospitalData() {}

    public static void loadHospitalData() {
        try{
            // CREATE A URL
            String baseUrl = "https://services7.arcgis.com/BuriUAtfp8cZMlDt/arcgis/rest/services/dev_waittimes_exb_hfl_6da46_c5905_9256e/FeatureServer/0/query";
            String parameters = "?f=json&where=1%3D1&outFields=*&resultRecordCount=100&returnGeometry=true&outSR=4326";
            String url = baseUrl + parameters;

            // INITIALIZE CLIENT
            HttpClient client = HttpClient.newBuilder()
                                          .connectTimeout(java.time.Duration.ofSeconds(10))
                                          .build();

            // CREATE A REQUEST
            java.net.http.HttpRequest request = HttpRequest.newBuilder()
                                                           .uri(URI.create(url))
                                                           .header("User-Agent", "Mozilla/5.0(Macintosh; Intel Mac OS X 10_15_7)")
                                                           .GET()
                                                           .build();

            // SEND THE REQUEST
            System.out.println("accessing ArcGIS ");
            HttpResponse<String> strResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            //ofFile
            //HttpResponse<Path> fileResponse = client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get("response.json")));
            //System.out.println("response saved to response.json");

            // PRINT THE RESPONSE
            if (strResponse.statusCode() == 200) {
                //
                JsonObject rootObj = JsonParser.parseString(strResponse.body()).getAsJsonObject();
                //
                JsonArray features = rootObj.getAsJsonArray("features");
                
                System.out.println("------Coordinates-----");

                int count = 1;
                for (JsonElement element : features) {
                    JsonObject feature = element.getAsJsonObject();
                    JsonObject attributes = feature.getAsJsonObject().getAsJsonObject("attributes");
                    JsonObject geometry = feature.getAsJsonObject().getAsJsonObject("geometry");

                    String name = attributes.get("hospital_name").getAsString();

                    String waitTime = "N/A";
                    if (attributes.has("ed_wait_range") && !attributes.get("ed_wait_range").isJsonNull()) {
                        waitTime = attributes.get("ed_wait_range").getAsString();
                    }

                    double longitude = geometry.get("x").getAsDouble();
                    double latitude = geometry.get("y").getAsDouble();

                    System.out.format("[%d]hosital: %s\n wait time: %s\n coordinates: (%.6f, %.6f)\n"
                                        , count++, name, waitTime, latitude, longitude);
                }
                System.out.println("------End of Coordinates-----");
            } else {
                System.out.println("Request failed with status code: " + strResponse.statusCode());
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }   
    
    }
}