package main;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import functions.HospitalData;

public class Main {
    public static double[] userLocation;
    public static void main(String[] args) {
        
        //INSTATIATE 
        String userPostalCode;
        Scanner scanner = new Scanner(System.in);

        //GET USER POSTAL CODE(CHECK FORMAT: LENGTH, INVALID, UPPERCASE)
        System.out.println("NB Hospital Distance Calculator\nEnter your postal code (format: A1A1A1): ");
        userPostalCode = scanner.nextLine();
        userPostalCode = userPostalCode.toUpperCase();

        //CALL FUNCTION TO GET COORDINATES FROM POSTAL CODE
        userLocation = functions.PostalConverter.getLocation(userPostalCode);
        while (userLocation == null) {
            System.out.println("Invalid postal code. Please enter a valid postal code (format: A1A1A1): ");
            userPostalCode = scanner.nextLine();
            userPostalCode = userPostalCode.toUpperCase();
            userLocation = functions.PostalConverter.getLocation(userPostalCode);
        }
        System.out.format("Your location: (%.6f, %.6f)\n", userLocation[0], userLocation[1]);

        //GET INSTATIATE HOSPITAL DATA
        functions.HospitalData.loadHospitalData();

        //CALCULATE DISTANCE TO EACH HOSPITAL AND FIND THE CLOSE
        String rawJson = functions.HospitalData.loadHospitalData();
        com.google.gson.JsonObject dataObj = com.google.gson.JsonParser.parseString(rawJson).getAsJsonObject();
        com.google.gson.JsonArray features = dataObj.getAsJsonArray("features");
        String closestHospitalName = "";
        double minDistance = Double.MAX_VALUE;

        for (com.google.gson.JsonElement element : features) {

            com.google.gson.JsonObject attributes = element.getAsJsonObject().get("attributes").getAsJsonObject();
            com.google.gson.JsonObject geometry = element.getAsJsonObject().get("geometry").getAsJsonObject();
    
            double hLat = geometry.get("y").getAsDouble();
            double hLon = geometry.get("x").getAsDouble();
    
            double dist = functions.Commute.getDistance(userLocation[0], userLocation[1], hLat, hLon);
            if (dist < minDistance) {
                minDistance = dist;
                closestHospitalName = attributes.get("hospital_name").getAsString();
            }
        }
        System.out.format("\n[Result] closest: %s | distance: %.2f km\n\n", closestHospitalName, minDistance);
        scanner.close();
    }
}