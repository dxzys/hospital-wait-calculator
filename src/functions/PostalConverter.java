package functions;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class PostalConverter {
    public PostalConverter() {}
    public static double[] getLocation(String postalCode) {
        
        try{
            //CREATE A URL
            String baseUrl = "https://geocoder.ca/";
            String parameters = "?postal=" + postalCode + "&geoit=XML&json=1";
            String url = baseUrl + parameters;

            //INITIALIZE CLIENT
            HttpClient client = HttpClient.newBuilder()
                                      .connectTimeout(java.time.Duration.ofSeconds(10))
                                      .build();

            //CREATE A REQUEST
            java.net.http.HttpRequest request = HttpRequest.newBuilder()
                                                           .uri(URI.create(url))
                                                           .header("User-Agent", "Mozilla/5.0(Macintosh; Intel Mac OS X 10_15_7)")
                                                           .GET()
                                                           .build();
            // SEND THE REQUEST
            System.out.println("accessing ArcGIS ");
            HttpResponse<String> strResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            // PRINT THE RESPONSE
            if (strResponse.statusCode() == 200) {
                JsonObject rootObj = JsonParser.parseString(strResponse.body()).getAsJsonObject();

                double lantitude = rootObj.get("latt").getAsDouble();
                double longitude = rootObj.get("longt").getAsDouble();
                return new double[]{lantitude, longitude};
            } else {
                System.out.println("Error fetching location data: " + strResponse.statusCode());
            }
            
        } catch (Exception e) {
            System.out.println("Error fetching location data: " + e.getMessage());
        }
        return null;
    }
}
